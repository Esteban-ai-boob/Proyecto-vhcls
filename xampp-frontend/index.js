let API_BASE_URL = localStorage.getItem('API_BASE_URL') || 'http://localhost:9001';
let jwtToken = '';
let apiKey = '';
let map, markersLayer, previewMarker;

// Initial Setup
document.addEventListener('DOMContentLoaded', () => {
    initMap();
    setupNavigation();
    setupEventListeners();
    setupPanels();
});

// --- Map Initialization ---
function initMap() {
    map = L.map('map', {
        zoomControl: false // Move to bottom right later
    }).setView([4.4389, -75.2322], 13); // Default Ibague

    L.control.zoom({ position: 'bottomright' }).addTo(map);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/attributions">CARTO</a>',
        subdomains: 'abcd',
        maxZoom: 20
    }).addTo(map);

    markersLayer = L.layerGroup().addTo(map);

    // Map click handling for adding stops
    map.on('click', async function(e) {
        if (!document.getElementById('panel-add-stop').classList.contains('active')) return;
        
        const lat = e.latlng.lat.toFixed(5);
        const lng = e.latlng.lng.toFixed(5);
        
        document.getElementById('addLat').value = lat;
        document.getElementById('addLng').value = lng;
        
        // Show preview marker
        if (previewMarker) map.removeLayer(previewMarker);
        const iconHtml = `<div class="stop-marker preview"><span>📍</span></div>`;
        const customIcon = L.divIcon({ html: iconHtml, className: '', iconSize: [30, 30], iconAnchor: [15, 15] });
        previewMarker = L.marker([lat, lng], { icon: customIcon }).addTo(map);
        
        // Reverse Geocoding via Nominatim
        document.getElementById('addLocation').value = 'Buscando dirección...';
        try {
            const res = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`);
            const data = await res.json();
            document.getElementById('addLocation').value = data.display_name || `${lat}, ${lng}`;
        } catch (err) {
            document.getElementById('addLocation').value = `Ibagué, Tolima, Colombia`; // Fallback
        }
    });
}

// --- Navigation ---
function setupNavigation() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', () => {
            // Update active state in nav
            document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
            item.classList.add('active');
            
            // Show corresponding panel
            const targetId = item.getAttribute('data-target');
            document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
            document.getElementById(targetId).classList.add('active');
            
            // Clear map preview marker if leaving add-stop
            if (targetId !== 'panel-add-stop' && previewMarker) {
                map.removeLayer(previewMarker);
                previewMarker = null;
            }

            // Handle RabbitMQ polling
            if (targetId === 'panel-rabbitmq') {
                fetchRabbitMQStatus();
                if (document.getElementById('rmqAutoRefresh').checked) {
                    startRabbitMQPolling();
                }
            } else {
                if (typeof stopRabbitMQPolling === 'function') {
                    stopRabbitMQPolling();
                }
            }
        });
    });

    // Result panel toggle
    const resultPanel = document.getElementById('resultPanel');
    const toggleIcon = document.getElementById('resultToggleIcon');
    document.getElementById('toggleResultBtn').addEventListener('click', () => {
        resultPanel.classList.toggle('open');
        toggleIcon.setAttribute('name', resultPanel.classList.contains('open') ? 'chevron-down-outline' : 'chevron-up-outline');
    });
}

function setupPanels() {
    // Open result panel by default to show it exists
    setTimeout(() => {
        document.getElementById('resultPanel').classList.add('open');
        document.getElementById('resultToggleIcon').setAttribute('name', 'chevron-down-outline');
    }, 1000);
}

// --- Event Listeners ---
function setupEventListeners() {
    document.getElementById('loginBtn').addEventListener('click', handleLogin);
    document.getElementById('loadRouteBtn').addEventListener('click', loadRoute);
    document.getElementById('driverRoutesBtn').addEventListener('click', getDriverRoutes);
    document.getElementById('plateRoutesBtn').addEventListener('click', getPlateRoutes);
    document.getElementById('blockedRoutesBtn').addEventListener('click', getBlockedRoutes);
    document.getElementById('addStopBtn').addEventListener('click', createStop);
    document.getElementById('uploadExcelBtn').addEventListener('click', uploadExcel);
    document.getElementById('sampleExcelBtn').addEventListener('click', simulateSampleExcel);
    document.getElementById('checkLoadBtn').addEventListener('click', checkExcelLoad);
    document.getElementById('viewGeneratedRouteBtn').addEventListener('click', viewGeneratedRoute);
    document.getElementById('fileInput').addEventListener('change', handleFileToBase64);
    document.getElementById('sendDocumentBtn').addEventListener('click', uploadDocumentBase64);
    
    // Admin Dashboard Handlers
    const navManagePersonas = document.getElementById('navManagePersonas');
    if (navManagePersonas) navManagePersonas.addEventListener('click', loadAdminPersonas);
    
    const navManageVehicles = document.getElementById('navManageVehicles');
    if (navManageVehicles) navManageVehicles.addEventListener('click', loadAdminVehicles);

    // Toggle Forms
    document.getElementById('btnShowCreatePersona')?.addEventListener('click', () => document.getElementById('formCreatePersona').classList.toggle('hidden'));
    document.getElementById('btnCancelPersona')?.addEventListener('click', () => document.getElementById('formCreatePersona').classList.add('hidden'));
    document.getElementById('btnSavePersona')?.addEventListener('click', createPersona);

    document.getElementById('btnShowCreateVehicle')?.addEventListener('click', () => {
        document.getElementById('formCreateVehicle').classList.toggle('hidden');
        loadAvailableDocuments();
    });
    document.getElementById('btnCancelVehicle')?.addEventListener('click', () => document.getElementById('formCreateVehicle').classList.add('hidden'));
    document.getElementById('btnSaveVehicle')?.addEventListener('click', createVehicle);

    
    const copyBtn = document.getElementById('copyBase64Btn');
    if (copyBtn) {
        copyBtn.addEventListener('click', () => {
            const text = document.getElementById('base64Text').value;
            if (!text) {
                showToast('No hay código Base64 para copiar', 'warning');
                return;
            }
            navigator.clipboard.writeText(text).then(() => {
                showToast('Código copiado al portapapeles', 'success');
            }).catch(() => {
                showToast('Error al copiar al portapapeles', 'error');
            });
        });
    }
    document.getElementById('refreshTasksBtn').addEventListener('click', () => { showToast('Recargando datos...', 'success'); loadRoute(); });

    // RabbitMQ Handlers
    const chkAutoRefresh = document.getElementById('rmqAutoRefresh');
    if (chkAutoRefresh) {
        chkAutoRefresh.addEventListener('change', (e) => {
            if (e.target.checked) {
                startRabbitMQPolling();
            } else {
                stopRabbitMQPolling();
            }
        });
    }
    const btnRefreshRabbitMQ = document.getElementById('btnRefreshRabbitMQ');
    if (btnRefreshRabbitMQ) {
        btnRefreshRabbitMQ.addEventListener('click', fetchRabbitMQStatus);
    }
    document.getElementById('inlineAddStopBtn').addEventListener('click', () => {
        const routeCode = document.getElementById('routeCode').value;
        if (!routeCode) return showToast('Ingresa un código de ruta', 'warning');
        document.querySelector('[data-target="panel-add-stop"]').click();
        document.getElementById('addRouteCode').value = routeCode;
    });
    document.getElementById('loadPersonasBtn').addEventListener('click', loadPersonas);
    document.getElementById('configApiBtn').addEventListener('click', () => {
        const currentUrl = localStorage.getItem('API_BASE_URL') || 'http://localhost:9001';
        const newUrl = prompt('Ingresa la URL del Backend (ej. http://localhost:9001 o la URL de tu túnel público):', currentUrl);
        if (newUrl !== null) {
            localStorage.setItem('API_BASE_URL', newUrl.trim());
            showToast('URL del API actualizada. Recargando...', 'success');
            setTimeout(() => window.location.reload(), 1000);
        }
    });
}

// --- Fetch Wrapper with Auth ---
async function fetchAPI(endpoint, method = 'GET', body = null) {
    const isLogin = endpoint === '/authenticate';
    const isPublic = endpoint.includes('/public/');
    const isDemo = endpoint.includes('/api/demo');
    
    const headers = {};
    if (!endpoint.includes('multipart/form-data')) {
        headers['Content-Type'] = 'application/json';
    }
    if (!isLogin && !isPublic && !isDemo) {
        if (!jwtToken) {
            showToast('No estás autenticado. Ve a la sección Autenticación primero.', 'warning');
            openResultPanel();
            updateResultPanel('Error', 'Falta Token JWT');
            throw new Error("No token");
        }
        headers['Authorization'] = `Bearer ${jwtToken}`;
        if (apiKey) headers['APIKey'] = apiKey;
    }

    const options = { method, headers };
    
    if (body) {
        if (body instanceof FormData) {
            delete headers['Content-Type']; // Let browser set boundary
            options.body = body;
        } else {
            options.body = JSON.stringify(body);
        }
    }

    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) loadingOverlay.classList.add('active');

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        let data;
        const text = await response.text();
        try {
            data = JSON.parse(text);
        } catch(e) {
            data = text;
        }

        updateResultPanel(response.status, JSON.stringify(data, null, 2));
        openResultPanel();

        if (!response.ok) {
            showToast(`Error ${response.status}`, 'error');
            throw new Error(data.message || data.error || `Error ${response.status}`);
        }
        
        return data;
    } catch (error) {
        if(error.message !== "No token") {
            showToast('Falla de conexión o error del servidor', 'error');
            updateResultPanel('Network Error', error.message);
            openResultPanel();
        }
        throw error;
    } finally {
        if (loadingOverlay) loadingOverlay.classList.remove('active');
    }
}

// --- UI Helpers ---
function openResultPanel() {
    const p = document.getElementById('resultPanel');
    if(!p.classList.contains('open')) {
        p.classList.add('open');
        document.getElementById('resultToggleIcon').setAttribute('name', 'chevron-down-outline');
    }
}

function syntaxHighlight(json) {
    if (typeof json !== 'string') return json;
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}

function updateResultPanel(status, dataStr) {
    document.getElementById('status').innerText = `Status: ${status}`;
    const resultOutput = document.getElementById('resultOutput');
    if (dataStr && (dataStr.trim().startsWith('{') || dataStr.trim().startsWith('['))) {
        resultOutput.innerHTML = syntaxHighlight(dataStr);
    } else {
        resultOutput.innerText = dataStr;
    }
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    let icon = 'checkmark-circle-outline';
    if(type === 'error') icon = 'close-circle-outline';
    if(type === 'warning') icon = 'alert-circle-outline';

    toast.innerHTML = `<ion-icon name="${icon}" style="font-size: 24px;"></ion-icon> <span>${message}</span>`;
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'toast-out 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// --- Feature Implementation ---

// 1. Auth
async function handleLogin() {
    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;
    apiKey = document.getElementById('apiKey').value;

    try {
        const data = await fetchAPI('/authenticate', 'POST', { username: user, password: pass });
        jwtToken = data.jwttoken;
        const badge = document.getElementById('authBadge');
        badge.className = 'badge ok';
        badge.innerText = 'Autenticado';
        showToast('Login exitoso', 'success');
    } catch (error) {
        console.error(error);
    }
}

// 2. Load Route (Main Visual)
async function loadRoute() {
    const code = document.getElementById('routeCode').value;
    if (!code) return showToast('Ingresa un código de ruta', 'warning');

    try {
        const data = await fetchAPI(`/LaboratorioV1/rutas/${code}`);
        // Backend returns RutaDetalleDTO { codigoRuta, placa, conductor, paradas: [...] }
        const stops = data.paradas || data;
        drawRouteOnMap(stops);
        showToast('Ruta cargada', 'success');
    } catch (e) {
        console.error(e);
        markersLayer.clearLayers();
        document.getElementById('stopList').innerHTML = '';
        document.getElementById('routeMetrics').classList.add('hidden');
    }
}

function drawRouteOnMap(stops) {
    markersLayer.clearLayers();
    document.getElementById('stopList').innerHTML = '';
    
    if (!stops || stops.length === 0) {
        showToast('La ruta no tiene paradas', 'warning');
        return;
    }

    const bounds = [];
    const listUl = document.getElementById('stopList');
    document.getElementById('routeMetrics').classList.remove('hidden');
    document.getElementById('stopCount').innerText = stops.length;
    
    let drawnCount = 0;
    
    stops.sort((a,b) => a.orden - b.orden).forEach((stop, index) => {
        // Add to sidebar list
        const li = document.createElement('li');
        li.className = 'stop-item';
        li.innerHTML = `
            <div class="stop-header">
                <div class="stop-order">${stop.orden}</div>
                <div class="stop-name">${stop.nombre_parada || stop.nombreParada || 'Parada'}</div>
                <button class="delete-stop-btn" data-id="${stop.id}" title="Eliminar parada">
                    <ion-icon name="trash-outline"></ion-icon>
                </button>
            </div>
            <div class="stop-location"><ion-icon name="location-outline"></ion-icon> ${stop.ubicacion}</div>
        `;
        listUl.appendChild(li);

        // Add to map if coordinates exist
        if (stop.latitud && stop.longitud && stop.latitud !== 0) {
            const lat = parseFloat(stop.latitud);
            const lng = parseFloat(stop.longitud);
            bounds.push([lat, lng]);
            drawnCount++;

            let mClass = 'middle';
            if (index === 0) mClass = 'start';
            else if (index === stops.length - 1) mClass = 'end';

            const iconHtml = `<div class="stop-marker ${mClass}"><span>${stop.orden}</span></div>`;
            const customIcon = L.divIcon({ html: iconHtml, className: '', iconSize: [30, 30], iconAnchor: [15, 15] });

            L.marker([lat, lng], { icon: customIcon })
                .bindPopup(`<strong>${stop.nombre_parada || stop.nombreParada}</strong><span>${stop.ubicacion}</span>`)
                .addTo(markersLayer);
                
            // Click list item to pan
            li.addEventListener('click', () => {
                map.flyTo([lat, lng], 16);
            });
        }
    });

    document.getElementById('mappedCount').innerText = drawnCount;
    document.getElementById('routeDistance').innerText = drawnCount > 1 ? (drawnCount * 1.2).toFixed(1) + 'km' : '0km'; // Fake distance

    // Handle delete stop buttons
    document.querySelectorAll('.delete-stop-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            const id = btn.getAttribute('data-id');
            if (confirm('¿Estás seguro de eliminar esta parada?')) {
                try {
                    await fetchAPI(`/LaboratorioV1/trayectos/${id}`, 'DELETE');
                    showToast('Parada eliminada', 'success');
                    loadRoute();
                } catch (err) {
                    showToast('Error al eliminar parada', 'error');
                }
            }
        });
    });

    // After adding markers, draw polyline connecting stops using OSRM
    if (bounds.length > 1) {
        drawOSRMRoute(bounds);
    }
    if (bounds.length > 0) {
        map.fitBounds(bounds, { padding: [50, 50] });
    }
}

async function drawOSRMRoute(bounds) {
    try {
        const coords = bounds.map(p => p[1]+','+p[0]).join(';');
        const url = `https://router.project-osrm.org/route/v1/driving/${coords}?overview=full&geometries=geojson`;
        const res = await fetch(url);
        const data = await res.json();
        if (data.routes && data.routes.length > 0) {
            const routeCoords = data.routes[0].geometry.coordinates.map(c => [c[1], c[0]]);
            L.polyline(routeCoords, { color: '#0ea5e9', weight: 5, opacity: 0.8 }).addTo(markersLayer);
            
            // Update real distance
            const distanceKm = (data.routes[0].distance / 1000).toFixed(1);
            document.getElementById('routeDistance').innerText = distanceKm + 'km';
        } else {
            // Fallback to straight line
            L.polyline(bounds, { color: '#ff7800', weight: 4, opacity: 0.7, dashArray: '10, 10' }).addTo(markersLayer);
        }
    } catch (e) {
        // Fallback to straight line
        L.polyline(bounds, { color: '#ff7800', weight: 4, opacity: 0.7, dashArray: '10, 10' }).addTo(markersLayer);
    }
}

// 3. Driver Routes
async function getDriverRoutes() {
    const id = document.getElementById('driverId').value;
    try {
        const data = await fetchAPI(`/LaboratorioV1/conductores/${id}/rutas`);
        updateResultPanel(200, JSON.stringify(data, null, 2));
        showToast('Consulta exitosa', 'success');
    } catch (e) { }
}

// 4. Vehicle Routes
async function getPlateRoutes() {
    const plate = document.getElementById('plate').value;
    try {
        const data = await fetchAPI(`/LaboratorioV1/vehiculos/${plate}/rutas`);
        updateResultPanel(200, JSON.stringify(data, null, 2));
        showToast('Consulta exitosa', 'success');
    } catch (e) { }
}

// 5. Blocked Routes
async function getBlockedRoutes() {
    try {
        const data = await fetchAPI(`/LaboratorioV1/rutas/bloqueadas`);
        updateResultPanel(200, JSON.stringify(data, null, 2));
        showToast('Consulta exitosa', 'success');
    } catch (e) { }
}

// 6. Add Stop
async function createStop() {
    const route = document.getElementById('addRouteCode').value;
    const name = document.getElementById('addName').value;
    const loc = document.getElementById('addLocation').value;
    const lat = document.getElementById('addLat').value;
    const lng = document.getElementById('addLng').value;

    if (!route || !name || !loc) return showToast('Completa todos los campos obligatorios', 'warning');
    if (!lat || !lng || lat.trim() === '' || lng.trim() === '') {
        return showToast('La Latitud y Longitud son obligatorias y no pueden estar vacías', 'error');
    }

    const payload = {
        codigoRuta: route,
        nombreParada: name,
        ubicacion: loc,
        latitud: parseFloat(lat),
        longitud: parseFloat(lng),
        vehiculoId: 1001, // As per requirements
        conductorId: 2    // As per requirements
    };

    try {
        await fetchAPI('/LaboratorioV1/trayectos', 'POST', payload);
        showToast('Parada agregada exitosamente', 'success');
        
        // Go back to route visualizer
        document.querySelector('[data-target="panel-route"]').click();
        document.getElementById('routeCode').value = route;
        loadRoute();
        
    } catch (e) { 
        showToast(e.message, 'error');
    }
}

// 7. Excel Upload
async function uploadExcel() {
    const fileInput = document.getElementById('excelInput');
    if (!fileInput.files.length) return showToast('Selecciona un archivo primero', 'warning');

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    try {
        const res = await fetchAPI('/api/excel/upload', 'POST', formData);
        showToast('Carga exitosa', 'success');
        
        if (res.idCargue) {
            document.getElementById('loadId').value = res.idCargue;
            checkExcelLoad();
        }
    } catch (e) {}
}

async function checkExcelLoad() {
    const id = document.getElementById('loadId').value;
    if(!id) return;
    
    try {
        const res = await fetchAPI(`/api/excel/cargue/${id}`);
        showToast('Estado consultado', 'success');
        
        // Try to guess the route code generated by the backend SP
        const generatedCode = "RUTA-CARGUE-" + id;
        document.getElementById('generatedRouteContainer').classList.remove('hidden');
        document.getElementById('generatedRouteCode').value = generatedCode;
    } catch (e) {}
}

function viewGeneratedRoute() {
    const code = document.getElementById('generatedRouteCode').value;
    document.querySelector('[data-target="panel-route"]').click();
    document.getElementById('routeCode').value = code;
    loadRoute();
}

// Fake Excel Creation for easy demo
function simulateSampleExcel() {
    const ws_data = [
        ["orden", "nombre", "ubicacion"],
        [1, "Exito", "Exito de la 80, Ibague, Tolima"],
        [2, "Panamericana", "Panamericana, Ibague, Tolima"],
        [3, "Mercacentro 10", "Mercacentro 10, El Poblado, Ibague"]
    ];
    const ws = XLSX.utils.aoa_to_sheet(ws_data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Sheet1");
    
    // Convert to file
    const wbout = XLSX.write(wb, {bookType:'xlsx',  type: 'binary'});
    function s2ab(s) {
        const buf = new ArrayBuffer(s.length);
        const view = new Uint8Array(buf);
        for (let i=0; i<s.length; i++) view[i] = s.charCodeAt(i) & 0xFF;
        return buf;
    }
    const blob = new Blob([s2ab(wbout)],{type:"application/octet-stream"});
    const file = new File([blob], "prueba_profesor.xlsx", {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});
    
    // Assign to input
    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(file);
    document.getElementById('excelInput').files = dataTransfer.files;
    showToast('Archivo de prueba generado, listo para Cargar', 'success');
}

// 8. Documents
function handleFileToBase64(e) {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = function(evt) {
        const base64Str = evt.target.result.split(',')[1];
        document.getElementById('base64Text').value = base64Str;
        showToast('Convertido a Base64', 'success');
    };
    reader.readAsDataURL(file);
}

async function uploadDocumentBase64() {
    const vId = document.getElementById('vehicleId').value;
    const dId = document.getElementById('documentId').value;
    const email = document.getElementById('email').value;
    const b64 = document.getElementById('base64Text').value;

    const payload = [{
        documentId: parseInt(dId),
        documentContent: b64,
        notificationEmail: email
    }];

    try {
        await fetchAPI(`/vehicles/${vId}/documents/batch-base64`, 'POST', payload);
        showToast('Documento enviado y notificado', 'success');
    } catch (e) {}
}
// 9. Personas / Usuarios
async function loadPersonas() {
    try {
        const data = await fetchAPI('/LaboratorioV1/personas');
        const listUl = document.getElementById('personasList');
        listUl.innerHTML = '';
        
        if (!data || data.length === 0) {
            listUl.innerHTML = '<li class="text-center">No hay personas registradas</li>';
            return;
        }

        data.forEach(p => {
            const li = document.createElement('li');
            li.className = 'stop-item'; // Reusing style
            li.innerHTML = `
                <div class="stop-header">
                    <div class="stop-name">${p.pnombre || 'Sin nombre'}</div>
                    <span class="badge ${p.tipoPersona === 'CONDUCTOR' ? 'ok' : ''}" style="margin-left: auto;">${p.tipoPersona}</span>
                </div>
                <div class="stop-location"><ion-icon name="card-outline"></ion-icon> ${p.tipoIdentificacion} ${p.numeroIdentificacion || 'N/A'}</div>
                ${p.fechaLicencia ? `<div class="stop-location mt-2"><ion-icon name="calendar-outline"></ion-icon> Licencia exp: ${p.fechaLicencia}</div>` : ''}
            `;
            listUl.appendChild(li);
        });
        showToast('Personas cargadas', 'success');
    } catch (e) {}
}

// --- ADMIN DASHBOARD LOGIC ---

// 1. Personas
let adminPersonasData = [];

async function loadAdminPersonas() {
    try {
        const data = await fetchAPI('/LaboratorioV1/personas');
        adminPersonasData = data || [];
        const tbody = document.getElementById('adminPersonasList');
        if(!tbody) return;
        tbody.innerHTML = '';
        
        if (!adminPersonasData.length) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center">No hay personas registradas</td></tr>';
            return;
        }

        adminPersonasData.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${p.tipoIdentificacion} ${p.numeroIdentificacion || 'N/A'}</td>
                <td>${p.pnombre || ''}</td>
                <td><span class="badge ${p.tipoPersona === 'CONDUCTOR' ? 'ok' : ''}">${p.tipoPersona}</span></td>
                <td>
                    <button class="action-btn" title="Editar" onclick="editPersona(${p.id})"><ion-icon name="create-outline"></ion-icon></button>
                    <button class="action-btn delete" title="Inhabilitar" onclick="deletePersona('${p.numeroIdentificacion}')"><ion-icon name="ban-outline"></ion-icon></button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {}
}

async function createPersona() {
    const id = document.getElementById('newPersonaId').value;
    const idType = document.getElementById('newPersonaIdType').value;
    const names = document.getElementById('newPersonaNames').value;
    const lastnames = document.getElementById('newPersonaLastnames').value;
    const type = document.getElementById('newPersonaType').value;

    if(!id || !names) return showToast('Identificación y nombres requeridos', 'warning');

    const payload = {
        numeroIdentificacion: id,
        tipoIdentificacion: idType,
        pnombre: names + (lastnames ? ' ' + lastnames : ''),
        tipoPersona: type
    };

    try {
        await fetchAPI('/LaboratorioV1/personas', 'POST', payload);
        showToast('Persona creada exitosamente', 'success');
        document.getElementById('formCreatePersona').classList.add('hidden');
        loadAdminPersonas();
    } catch(e) {}
}

async function deletePersona(id) {
    if(!confirm(`¿Estás seguro de inhabilitar/eliminar a la persona ${id}?`)) return;
    try {
        await fetchAPI(`/LaboratorioV1/personas/${id}`, 'DELETE');
        showToast('Persona eliminada', 'success');
        loadAdminPersonas();
    } catch(e) {}
}

async function editPersona(id) {
    const person = adminPersonasData.find(p => p.id === id);
    if(!person) return showToast('Persona no encontrada en memoria', 'error');

    const newName = prompt('Modificar nombre completo:', person.pnombre || '');
    if(newName === null) return;

    // Preserve all original fields to avoid nulling them out in the backend
    const payload = {
        ...person,
        pnombre: newName.trim() || person.pnombre
    };

    // Spring Boot sometimes serializes LocalDate as an array [YYYY, MM, DD].
    // If we send it back as an array, the Java LocalDate.parse() will throw a 500 error.
    if (Array.isArray(payload.fechaLicencia)) {
        payload.fechaLicencia = `${payload.fechaLicencia[0]}-${String(payload.fechaLicencia[1]).padStart(2, '0')}-${String(payload.fechaLicencia[2]).padStart(2, '0')}`;
    }

    try {
        await fetchAPI(`/LaboratorioV1/personas/${id}`, 'PUT', payload);
        showToast('Persona actualizada', 'success');
        loadAdminPersonas();
    } catch(e) {}
}

// 2. Vehiculos
async function loadAvailableDocuments() {
    try {
        const data = await fetchAPI('/documents');
        const docSelect = document.getElementById('newVehicleDocId');
        if(!docSelect) return;
        
        docSelect.innerHTML = '';
        if(!data || data.length === 0) {
            docSelect.innerHTML = '<option value="">No hay documentos creados en BD</option>';
            return;
        }

        data.forEach(d => {
            const opt = document.createElement('option');
            opt.value = d.id;
            opt.textContent = `[ID: ${d.id}] ${d.documentName} (aplica a ${d.appliesToType})`;
            docSelect.appendChild(opt);
        });
    } catch(e) {
        console.error(e);
        document.getElementById('newVehicleDocId').innerHTML = '<option value="">Error al cargar documentos</option>';
    }
}
async function loadAdminVehicles() {
    try {
        const data = await fetchAPI('/vehicles');
        const tbody = document.getElementById('adminVehiclesList');
        if(!tbody) return;
        tbody.innerHTML = '';
        
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">No hay vehículos registrados</td></tr>';
            return;
        }

        data.forEach(v => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>${v.licensePlate}</strong></td>
                <td>${v.vehicleType}</td>
                <td>${v.serviceType}</td>
                <td>${v.fuelType}</td>
                <td>
                    <button class="action-btn delete" title="Eliminar" onclick="deleteVehicle(${v.id})"><ion-icon name="trash-outline"></ion-icon></button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {}
}

async function createVehicle() {
    const plate = document.getElementById('newVehiclePlate').value.trim().toUpperCase();
    const type = document.getElementById('newVehicleType').value;
    const service = document.getElementById('newVehicleService').value;
    const fuel = document.getElementById('newVehicleFuel').value;
    const docId = document.getElementById('newVehicleDocId').value;

    if(!plate || !docId) return showToast('Placa y Documento son obligatorios', 'warning');

    // Validacion estricta para evitar Error 400 del Backend
    if (type === 'AUTOMOVIL' && !/^[A-Z]{3}[0-9]{3}$/.test(plate)) {
        return showToast('Placa inválida. Automóvil requiere 3 letras y 3 números (Ej: ABC123)', 'error');
    } else if (type === 'MOTOCICLETA' && !/^[A-Z]{3}[0-9]{2}[A-Z]$/.test(plate)) {
        return showToast('Placa inválida. Moto requiere 3 letras, 2 números, 1 letra (Ej: ABC12D)', 'error');
    }

    const payload = {
        vehicle: {
            licensePlate: plate,
            vehicleType: type,
            serviceType: service,
            fuelType: fuel,
            passengerCapacity: 4,
            modelYear: 2024,
            color: "#FFFFFF",
            brand: "Generico",
            line: "Estandar"
        },
        documentIds: [parseInt(docId)]
    };

    try {
        await fetchAPI('/vehicles', 'POST', payload);
        showToast('Vehículo creado exitosamente', 'success');
        document.getElementById('formCreateVehicle').classList.add('hidden');
        loadAdminVehicles();
    } catch(e) {}
}

async function deleteVehicle(id) {
    if(!confirm(`¿Estás seguro de eliminar el vehículo con ID ${id}?`)) return;
    try {
        await fetchAPI(`/vehicles/${id}`, 'DELETE');
        showToast('Vehículo eliminado', 'success');
        loadAdminVehicles();
    } catch(e) {}
}

// --- RABBITMQ MONITORING ---
let rabbitMQInterval = null;

async function fetchRabbitMQStatus() {
    try {
        const data = await fetchAPI('/api/v1/rabbitmq/status');
        
        document.getElementById('rmqInQueue').textContent = data.mensajesEnCola;
        document.getElementById('rmqProcessed').textContent = data.mensajesProcesados;
        document.getElementById('rmqConsumers').textContent = data.consumidoresActivos;
        
        const msgContainer = document.getElementById('rmqLatestMessages');
        if (data.ultimosMensajes && data.ultimosMensajes.length > 0) {
            msgContainer.innerHTML = data.ultimosMensajes.map(msg => 
                `<div style="margin-bottom: 8px; border-bottom: 1px solid #333; padding-bottom: 4px;">&gt; ${msg}</div>`
            ).join('');
        } else {
            msgContainer.innerHTML = '<p style="color: var(--text-muted);">No hay mensajes procesados aún.</p>';
        }
    } catch (error) {
        console.error('Error fetching RabbitMQ status:', error);
    }
}

function startRabbitMQPolling() {
    stopRabbitMQPolling();
    rabbitMQInterval = setInterval(fetchRabbitMQStatus, 2000);
}

function stopRabbitMQPolling() {
    if (rabbitMQInterval) {
        clearInterval(rabbitMQInterval);
        rabbitMQInterval = null;
    }
}
