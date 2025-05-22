document.addEventListener('DOMContentLoaded', () => {
  const hero = document.getElementById('hero');
  if (hero) {
    const backgrounds = [
       '/bg-techs.webp',
       '/image/fondo_electrico.webp',
        '/image/mujer2.webp',
       '/image/fondo_plomero.webp',
       '/image/mujer1.webp',
       '/image/fondo_electrodomesticos.webp'
    ];
    let index = 0;
    setInterval(() => {
      index = (index + 1) % backgrounds.length;
      hero.style.backgroundImage = `url('${backgrounds[index]}')`;
    }, 6000);
  }

  // Activar menús con hover
  const dropdowns = document.querySelectorAll('.navbar .dropdown');
  dropdowns.forEach(dropdown => {
    dropdown.addEventListener('mouseenter', () => {
      const menu = dropdown.querySelector('.dropdown-menu');
      dropdown.classList.add('show');
      menu.classList.add('show');
    });
    dropdown.addEventListener('mouseleave', () => {
      const menu = dropdown.querySelector('.dropdown-menu');
      dropdown.classList.remove('show');
      menu.classList.remove('show');
    });
  });
});
