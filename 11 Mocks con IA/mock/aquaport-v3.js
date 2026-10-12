// Iconos vectoriales propios de AquaPort y utilidades de render compartidas por los mocks Enterprise (v3).
document.body.insertAdjacentHTML('afterbegin', `
<svg width="0" height="0" style="position:absolute"><defs>
  <symbol id="i-drone" viewBox="0 0 32 32"><path d="M5 19c3 0 3-2 6-2s3 2 6 2 3-2 6-2 3 2 4 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M9 14h14l-2-5h-10z" fill="currentColor" opacity=".9"/><circle cx="16" cy="11" r="1.6" fill="#061428"/><path d="M7 24c3 0 3-2 6-2s3 2 6 2 3-2 6-2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" opacity=".45"/></symbol>
  <symbol id="i-surf" viewBox="0 0 32 32"><path d="M4 20c3 0 3-2 6-2s3 2 6 2 3-2 6-2 3 2 6 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M8 15h16l-2-5H10z" fill="currentColor"/></symbol>
  <symbol id="i-semi" viewBox="0 0 32 32"><path d="M4 14c3 0 3-2 6-2s3 2 6 2 3-2 6-2 3 2 6 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M9 13h14v6a7 4 0 0 1-14 0z" fill="currentColor"/></symbol>
  <symbol id="i-diver" viewBox="0 0 32 32"><path d="M4 8c3 0 3-2 6-2s3 2 6 2 3-2 6-2 3 2 6 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" opacity=".5"/><ellipse cx="16" cy="19" rx="8" ry="5" fill="currentColor"/><path d="M24 19l4-3v6z" fill="currentColor"/><circle cx="12" cy="19" r="1.6" fill="#061428"/><path d="M16 12v-3" stroke="currentColor" stroke-width="2"/></symbol>
  <symbol id="i-pin" viewBox="0 0 24 24"><path d="M12 22s7-6.2 7-12a7 7 0 1 0-14 0c0 5.8 7 12 7 12z" fill="none" stroke="currentColor" stroke-width="2"/><circle cx="12" cy="10" r="2.5" fill="currentColor"/></symbol>
  <symbol id="i-clock" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="2"/><path d="M12 7v5l3 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></symbol>
  <symbol id="i-bolt" viewBox="0 0 24 24"><path d="M13 2 4 14h7l-1 8 9-12h-7z" fill="currentColor"/></symbol>
  <symbol id="i-check" viewBox="0 0 24 24"><path d="M5 12.5l4.5 4.5L19 7.5" fill="none" stroke="currentColor" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"/></symbol>
  <symbol id="i-x" viewBox="0 0 24 24"><path d="M6 6l12 12M18 6 6 18" stroke="currentColor" stroke-width="2.4" stroke-linecap="round"/></symbol>
  <symbol id="i-alert" viewBox="0 0 24 24"><path d="M12 3 2 21h20z" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/><path d="M12 10v5M12 18v.5" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></symbol>
  <symbol id="i-stop" viewBox="0 0 24 24"><path d="M8 3h8l5 5v8l-5 5H8l-5-5V8z" fill="none" stroke="currentColor" stroke-width="2"/><path d="M9 9h6v6H9z" fill="currentColor"/></symbol>
  <symbol id="i-wave" viewBox="0 0 24 24"><path d="M2 9c2.5 0 2.5-2 5-2s2.5 2 5 2 2.5-2 5-2 2.5 2 5 2M2 16c2.5 0 2.5-2 5-2s2.5 2 5 2 2.5-2 5-2 2.5 2 5 2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></symbol>
  <symbol id="i-depth" viewBox="0 0 24 24"><path d="M12 3v15M7 13l5 5 5-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M4 21h16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></symbol>
  <symbol id="i-temp" viewBox="0 0 24 24"><path d="M10 14V5a2 2 0 0 1 4 0v9a4 4 0 1 1-4 0z" fill="none" stroke="currentColor" stroke-width="2"/></symbol>
  <symbol id="i-box" viewBox="0 0 24 24"><path d="M3 7l9-4 9 4v10l-9 4-9-4z M3 7l9 4 9-4 M12 11v10" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/></symbol>
  <symbol id="i-flask" viewBox="0 0 24 24"><path d="M9 3h6M10 3v6l-5 9a2 2 0 0 0 1.7 3h10.6a2 2 0 0 0 1.7-3l-5-9V3" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/><path d="M7.5 15h9" stroke="currentColor" stroke-width="2"/></symbol>
  <symbol id="i-shield" viewBox="0 0 24 24"><path d="M12 2 20 5v6c0 5-3.5 9-8 11-4.5-2-8-6-8-11V5z" fill="none" stroke="currentColor" stroke-width="2"/><path d="M8.5 12l2.5 2.5 4.5-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></symbol>
  <symbol id="i-route" viewBox="0 0 24 24"><circle cx="5" cy="18" r="2.5" fill="currentColor"/><circle cx="19" cy="6" r="2.5" fill="currentColor"/><path d="M7 17c6 0 2-9 10-10" fill="none" stroke="currentColor" stroke-width="2" stroke-dasharray="3 3"/></symbol>
  <symbol id="i-menu" viewBox="0 0 24 24"><path d="M4 7h16M4 12h16M4 17h16" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"/></symbol>
  <symbol id="i-map" viewBox="0 0 24 24"><path d="M3 6l6-2 6 2 6-2v14l-6 2-6-2-6 2z M9 4v14 M15 6v14" fill="none" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/></symbol>
  <symbol id="i-lock" viewBox="0 0 24 24"><rect x="5" y="10" width="14" height="11" rx="2" fill="none" stroke="currentColor" stroke-width="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3" fill="none" stroke="currentColor" stroke-width="2"/></symbol>
  <symbol id="i-swap" viewBox="0 0 24 24"><path d="M4 8h14l-3-3M20 16H6l3 3" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></symbol>
  <symbol id="i-chart" viewBox="0 0 24 24"><path d="M4 20V10M10 20V4M16 20v-7M22 20H2" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></symbol>
  <symbol id="i-spark" viewBox="0 0 24 24"><path d="M12 2l2.2 6.3L20 10l-5.8 1.7L12 18l-2.2-6.3L4 10l5.8-1.7z" fill="currentColor"/></symbol>
</defs></svg>`);

const ESTADOS = {
  DISPONIBLE:    {txt:'DISPONIBLE',    c:'var(--st-disponible)',   bg:'rgba(46,211,160,.14)'},
  EN_MISION:     {txt:'EN MISIÓN',     c:'var(--st-mision)',       bg:'rgba(59,130,246,.16)'},
  RECARGANDO:    {txt:'RECARGANDO',    c:'var(--st-recargando)',   bg:'rgba(245,184,65,.14)'},
  MANTENIMIENTO: {txt:'MANTENIMIENTO', c:'var(--st-mantenimiento)',bg:'rgba(138,153,173,.16)'},
  FALLO:         {txt:'FALLO',         c:'var(--st-fallo)',        bg:'rgba(242,84,91,.16)'},
  SUMERGIDO:     {txt:'SUMERGIDO',     c:'var(--st-sumergido)',    bg:'rgba(109,124,255,.18)'}
};
const TIPOS = {
  SUPERFICIAL:   {nombre:'Superficial',   icono:'i-surf',  modelo:'Aqua-Surf 200',  detalle:'Agua tranquila · hasta 500 g'},
  SEMISUMERGIDO: {nombre:'Semisumergido', icono:'i-semi',  modelo:'Aqua-Semi 300',  detalle:'Agua agitada · hasta 1,5 kg'},
  BUCEADOR:      {nombre:'Buceador',      icono:'i-diver', modelo:'Aqua-Diver 400', detalle:'Profundidad · hasta 300 g'}
};
const ic = (id, w = 18) => `<svg width="${w}" height="${w}"><use href="#${id}"/></svg>`;
const colorBat = b => b >= 60 ? 'var(--bat-alta)' : b >= 35 ? 'var(--bat-media)' : 'var(--bat-baja)';


const ZONAS = {
  EMBALSE_INVESTIGACION: {nombre:'Embalse de Investigación', corto:'Embalse', c:'var(--zona-embalse)', hex:'#6D7CFF'},
  RED_CANALES:           {nombre:'Red de Canales',           corto:'Canales', c:'var(--zona-canales)', hex:'#19C3E6'},
  LAGUNA_RESERVA:        {nombre:'Laguna de Reserva',        corto:'Laguna',  c:'var(--zona-laguna)',  hex:'#2ED3A0'},
  LAB_CENTRAL:           {nombre:'Laboratorio Hídrico Central', corto:'Lab. Central', c:'var(--zona-lab)', hex:'#F5B841'}
};
// Los 60 drones de FlotaEnterprise.java: misma fórmula de batería y misma ubicación.
const ESTADOS_MOCK = {3:'RECARGANDO',7:'SUMERGIDO',12:'EN_MISION',18:'MANTENIMIENTO',26:'EN_MISION',29:'FALLO',44:'EN_MISION',57:'RECARGANDO'};
const FLOTA = Array.from({length:60}, (_, i) => {
  const n = i + 1, grupo = Math.floor(i / 20), pos = i % 20;
  const tipo = ['BUCEADOR','SUPERFICIAL','SEMISUMERGIDO'][grupo];
  const natural = ['EMBALSE_INVESTIGACION','RED_CANALES','LAGUNA_RESERVA'][grupo];
  return {id:'AR-' + String(n).padStart(2,'0'), tipo, bat:30 + (n * 37) % 70,
          zona: pos < 15 ? natural : 'LAB_CENTRAL', estado: ESTADOS_MOCK[n] || 'DISPONIBLE'};
});

function navHTML(activo) {
  return `<nav class="nav">
    <div class="brand">
      <svg width="42" height="46" viewBox="0 0 44 48"><defs><linearGradient id="crest" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#19C3E6"/><stop offset="1" stop-color="#2ED3A0"/></linearGradient></defs>
        <path d="M22 2 40 9v14c0 12-8 19-18 23C12 42 4 35 4 23V9z" fill="url(#crest)"/><path d="M22 7 35 12v11c0 9-6 14.5-13 17.6C15 37.5 9 32 9 23V12z" fill="#061428"/>
        <path d="M22 13c4 5 6 8.2 6 11a6 6 0 0 1-12 0c0-2.8 2-6 6-11z" fill="url(#crest)"/><path d="M11 31c2.6 0 2.6-1.6 5.5-1.6s2.9 1.6 5.5 1.6 2.6-1.6 5.5-1.6 2.9 1.6 5.5 1.6" stroke="#19C3E6" stroke-width="2" fill="none" stroke-linecap="round"/></svg>
      <div><div class="brand-name">AQUA<span>PORT</span> <span style="font-size:15px;color:var(--zona-lab)">ENTERPRISE</span></div><div class="brand-sub">Red multi-embalse · 60 drones · ECI</div></div>
    </div>
    <div class="links">${['Mapa','Rutas','Monitoreo','Alertas','Custodia'].map(l => `<a href="#" class="${l===activo?'on':''}">${l}</a>`).join('')}</div>
    <div class="nav-right">
      <div class="live"><span class="dot pulse"></span>EN VIVO · 14:06</div>
      <button class="btn-emergencia" title="Detiene todas las rutas en curso">${ic('i-stop',18)}EMERGENCIA</button>
      <div class="avatar">OH</div>
    </div>
    <button class="burger" aria-label="Menú">${ic('i-menu',22)}</button>
  </nav>`;
}
const zchip = z => `<span class="zchip" style="--z:${ZONAS[z].c}"><i></i>${ZONAS[z].corto}</span>`;
const tipoIc = (t, w = 18) => ic(TIPOS[t].icono, w);
