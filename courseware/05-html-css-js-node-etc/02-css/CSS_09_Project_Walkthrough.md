# CSS Module 09 — Full Project Walkthrough

## Project Overview

We will build a complete enterprise-style web page with:
- A responsive sticky header with logo and navigation
- A hero section with headline, CTA buttons, and background image
- A news & events section with a card grid
- A multi-column footer

This module integrates everything from the HTML and CSS courseware into one cohesive project.

---

## Project Structure

```
src/main/resources/
├── static/
│   └── css/
│       ├── normalize.css
│       ├── tokens.css        ← Design tokens (custom properties)
│       ├── base.css          ← Reset + base element styles
│       ├── layout.css        ← Header, footer, grid containers
│       ├── components.css    ← Buttons, cards, badges
│       └── main.css          ← @import orchestrator
└── templates/
    └── index.html
```

---

## HTML: Coding the Full Page Structure

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="Acme Corp — Enterprise software solutions for modern businesses.">
  
  <title>Acme Corp — Enterprise Solutions</title>
  
  <!-- Preconnect for performance -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  
  <!-- Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  
  <!-- Stylesheets -->
  <link rel="stylesheet" href="/css/normalize.css">
  <link rel="stylesheet" href="/css/main.css">
  
  <!-- Favicon -->
  <link rel="icon" type="image/svg+xml" href="/images/favicon.svg">
</head>

<body>
  <!-- Skip to main content (accessibility) -->
  <a href="#main-content" class="skip-link">Skip to main content</a>

  <!-- ═══════════════════════════════════════════
       HEADER
       ═══════════════════════════════════════════ -->
  <header class="site-header" role="banner">
    <div class="container">
      <a href="/" class="site-logo" aria-label="Acme Corp home">
        <img src="/images/logo.svg" alt="Acme Corp" width="140" height="40">
      </a>

      <!-- Mobile menu toggle -->
      <button class="nav-toggle" 
              aria-expanded="false" 
              aria-controls="main-nav"
              aria-label="Open navigation menu">
        <span class="nav-toggle__bar"></span>
        <span class="nav-toggle__bar"></span>
        <span class="nav-toggle__bar"></span>
      </button>

      <nav id="main-nav" class="main-nav" aria-label="Main navigation">
        <ul class="main-nav__list" role="list">
          <li><a href="/" class="main-nav__link" aria-current="page">Home</a></li>
          <li><a href="/products" class="main-nav__link">Products</a></li>
          <li><a href="/services" class="main-nav__link">Services</a></li>
          <li><a href="/about" class="main-nav__link">About</a></li>
          <li><a href="/blog" class="main-nav__link">Blog</a></li>
        </ul>
      </nav>

      <div class="header-cta">
        <a href="/login" class="btn btn--ghost btn--sm">Log In</a>
        <a href="/signup" class="btn btn--primary btn--sm">Get Started</a>
      </div>
    </div>
  </header>

  <!-- ═══════════════════════════════════════════
       MAIN CONTENT
       ═══════════════════════════════════════════ -->
  <main id="main-content">

    <!-- HERO SECTION -->
    <section class="hero" aria-labelledby="hero-heading">
      <div class="hero__content">
        <span class="hero__eyebrow">Enterprise Solutions</span>
        <h1 class="hero__heading" id="hero-heading">
          Build Faster.<br>Scale Smarter.
        </h1>
        <p class="hero__description">
          Acme Corp delivers battle-tested software solutions that power 
          Fortune 500 companies. From microservices to full-stack platforms, 
          we help you ship with confidence.
        </p>
        <div class="hero__actions">
          <a href="/signup" class="btn btn--primary btn--lg">Start Free Trial</a>
          <a href="/demo" class="btn btn--outline btn--lg">Watch Demo</a>
        </div>
        <p class="hero__note">No credit card required. 14-day free trial.</p>
      </div>
      <div class="hero__image" aria-hidden="true">
        <img src="/images/hero-dashboard.png" 
             alt=""
             width="600" height="450"
             fetchpriority="high">
      </div>
    </section>

    <!-- TRUST BAR -->
    <section class="trust-bar" aria-label="Trusted by leading companies">
      <div class="container">
        <p class="trust-bar__label">Trusted by teams at</p>
        <ul class="trust-bar__logos" role="list" aria-label="Company logos">
          <li><img src="/images/logos/tcs.svg" alt="Tata Consultancy Services" width="120" height="32"></li>
          <li><img src="/images/logos/infosys.svg" alt="Infosys" width="100" height="32"></li>
          <li><img src="/images/logos/wipro.svg" alt="Wipro" width="80" height="32"></li>
          <li><img src="/images/logos/hcl.svg" alt="HCL Technologies" width="80" height="32"></li>
          <li><img src="/images/logos/tech-mahindra.svg" alt="Tech Mahindra" width="130" height="32"></li>
        </ul>
      </div>
    </section>

    <!-- NEWS & EVENTS SECTION -->
    <section class="news-events" aria-labelledby="news-heading">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title" id="news-heading">News & Events</h2>
          <a href="/blog" class="btn btn--ghost">View All Posts →</a>
        </div>

        <div class="news-grid">
          <!-- Featured article -->
          <article class="news-card news-card--featured" aria-labelledby="news-1-title">
            <a href="/blog/spring-boot-3" class="news-card__image-link" tabindex="-1" aria-hidden="true">
              <img src="/images/blog/spring-boot-3.jpg" 
                   alt=""
                   class="news-card__image"
                   width="800" height="450"
                   loading="lazy">
            </a>
            <div class="news-card__body">
              <div class="news-card__meta">
                <span class="badge badge--blue">Engineering</span>
                <time datetime="2024-11-15" class="news-card__date">15 Nov 2024</time>
              </div>
              <h3 class="news-card__title" id="news-1-title">
                <a href="/blog/spring-boot-3">
                  Migrating to Spring Boot 3: What Java Developers Need to Know
                </a>
              </h3>
              <p class="news-card__excerpt">
                Spring Boot 3 brings Java 17 as baseline, virtual threads support, 
                native compilation, and much more. Here's what changes and how to migrate.
              </p>
              <footer class="news-card__footer">
                <img src="/images/authors/jane.jpg" alt="" width="32" height="32" class="news-card__author-avatar">
                <span class="news-card__author">Jane Smith</span>
                <span class="news-card__read-time">8 min read</span>
              </footer>
            </div>
          </article>

          <!-- Regular articles -->
          <article class="news-card" aria-labelledby="news-2-title">
            <a href="/blog/kafka-streams" class="news-card__image-link" tabindex="-1" aria-hidden="true">
              <img src="/images/blog/kafka-streams.jpg" 
                   alt=""
                   class="news-card__image"
                   width="400" height="225"
                   loading="lazy">
            </a>
            <div class="news-card__body">
              <div class="news-card__meta">
                <span class="badge badge--purple">Architecture</span>
                <time datetime="2024-11-10">10 Nov 2024</time>
              </div>
              <h3 class="news-card__title" id="news-2-title">
                <a href="/blog/kafka-streams">
                  Real-Time Analytics with Kafka Streams and Spring Boot
                </a>
              </h3>
              <p class="news-card__excerpt">
                Build a real-time dashboard processing millions of events per second.
              </p>
            </div>
          </article>

          <!-- Event card -->
          <article class="news-card news-card--event" aria-labelledby="event-1-title">
            <div class="event-date-badge" aria-hidden="true">
              <span class="event-date-badge__month">DEC</span>
              <span class="event-date-badge__day">10</span>
            </div>
            <div class="news-card__body">
              <div class="news-card__meta">
                <span class="badge badge--green">Webinar</span>
                <span>Online · Free</span>
              </div>
              <h3 class="news-card__title" id="event-1-title">
                <a href="/events/microservices-webinar">
                  Microservices Design Patterns: Lessons from Production
                </a>
              </h3>
              <p class="news-card__excerpt">
                Join our principal engineer for a live deep-dive into patterns 
                that work at scale.
              </p>
              <footer class="news-card__footer">
                <time datetime="2024-12-10T14:00+05:30">
                  10 Dec 2024 · 2:00 PM IST
                </time>
                <a href="/events/microservices-webinar/register" class="btn btn--primary btn--sm">
                  Register Free
                </a>
              </footer>
            </div>
          </article>
        </div>
      </div>
    </section>

  </main>

  <!-- ═══════════════════════════════════════════
       FOOTER
       ═══════════════════════════════════════════ -->
  <footer class="site-footer" role="contentinfo">
    <div class="container">
      <div class="footer__grid">
        
        <!-- Brand column -->
        <div class="footer__brand">
          <a href="/" aria-label="Acme Corp home">
            <img src="/images/logo-white.svg" alt="Acme Corp" width="120" height="36">
          </a>
          <p class="footer__tagline">
            Enterprise software solutions for modern businesses.
          </p>
          <div class="footer__social">
            <a href="https://linkedin.com/company/acme" 
               target="_blank" rel="noopener noreferrer"
               aria-label="Acme Corp on LinkedIn">
              <svg aria-hidden="true" focusable="false" width="20" height="20"><!-- LinkedIn SVG --></svg>
            </a>
            <a href="https://twitter.com/acmecorp"
               target="_blank" rel="noopener noreferrer"
               aria-label="Acme Corp on Twitter">
              <svg aria-hidden="true" focusable="false" width="20" height="20"><!-- Twitter SVG --></svg>
            </a>
            <a href="https://github.com/acmecorp"
               target="_blank" rel="noopener noreferrer"
               aria-label="Acme Corp on GitHub">
              <svg aria-hidden="true" focusable="false" width="20" height="20"><!-- GitHub SVG --></svg>
            </a>
          </div>
        </div>

        <!-- Navigation columns -->
        <nav class="footer__nav" aria-label="Products">
          <h2 class="footer__nav-title">Products</h2>
          <ul role="list">
            <li><a href="/products/platform">Platform</a></li>
            <li><a href="/products/analytics">Analytics</a></li>
            <li><a href="/products/integrations">Integrations</a></li>
            <li><a href="/pricing">Pricing</a></li>
          </ul>
        </nav>

        <nav class="footer__nav" aria-label="Company">
          <h2 class="footer__nav-title">Company</h2>
          <ul role="list">
            <li><a href="/about">About Us</a></li>
            <li><a href="/blog">Blog</a></li>
            <li><a href="/careers">Careers</a></li>
            <li><a href="/press">Press</a></li>
          </ul>
        </nav>

        <nav class="footer__nav" aria-label="Resources">
          <h2 class="footer__nav-title">Resources</h2>
          <ul role="list">
            <li><a href="/docs">Documentation</a></li>
            <li><a href="/api">API Reference</a></li>
            <li><a href="/support">Support</a></li>
            <li><a href="/status">System Status</a></li>
          </ul>
        </nav>

        <!-- Contact column -->
        <div class="footer__contact">
          <h2 class="footer__nav-title">Contact</h2>
          <address>
            <p>
              <a href="mailto:hello@acme.com">hello@acme.com</a>
            </p>
            <p>
              <a href="tel:+918012345678">+91 80 1234 5678</a>
            </p>
            <p>
              Level 12, Tower B<br>
              UB City, Bengaluru 560001<br>
              Karnataka, India
            </p>
          </address>
        </div>
      </div>

      <div class="footer__bottom">
        <p>
          <small>
            © <time datetime="2024">2024</time> Acme Corp Pvt. Ltd. 
            All rights reserved.
          </small>
        </p>
        <nav class="footer__legal" aria-label="Legal">
          <a href="/privacy">Privacy Policy</a>
          <a href="/terms">Terms of Service</a>
          <a href="/cookies">Cookie Policy</a>
        </nav>
      </div>
    </div>
  </footer>
</body>
</html>
```

---

## CSS: General Styles & Base

```css
/* base.css */
*, *::before, *::after { box-sizing: border-box; }
* { margin: 0; }

html {
  font-size: 16px;
  scroll-behavior: smooth;
}

@media (prefers-reduced-motion: reduce) {
  html { scroll-behavior: auto; }
}

body {
  font-family: var(--font-sans);
  font-size: var(--text-base);
  line-height: var(--leading-relaxed);
  color: var(--color-text);
  background-color: var(--color-bg);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

img, picture, video, canvas, svg {
  display: block;
  max-width: 100%;
}

input, button, textarea, select { font: inherit; }

h1, h2, h3, h4, h5, h6 {
  line-height: var(--leading-tight);
  font-weight: var(--font-bold);
  color: var(--color-text);
}

a {
  color: var(--color-link);
  text-decoration-skip-ink: auto;
}

a:focus-visible {
  outline: 3px solid var(--color-focus);
  outline-offset: 2px;
  border-radius: var(--radius-sm);
}

/* Container */
.container {
  width: 100%;
  max-width: var(--container-xl);
  margin-inline: auto;
  padding-inline: var(--space-4);
}

@media (min-width: 640px) {
  .container { padding-inline: var(--space-6); }
}
@media (min-width: 1024px) {
  .container { padding-inline: var(--space-8); }
}

/* Skip link */
.skip-link {
  position: absolute;
  left: -9999px;
  top: var(--space-2);
  padding: var(--space-2) var(--space-4);
  background: var(--color-primary);
  color: white;
  text-decoration: none;
  border-radius: var(--radius-md);
  font-weight: var(--font-medium);
  z-index: var(--z-toast);
}
.skip-link:focus { left: var(--space-4); }
```

---

## CSS: Styling the Header

```css
/* layout/header.css */

.site-header {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  background: white;
  border-bottom: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.site-header .container {
  display: flex;
  align-items: center;
  gap: var(--space-6);
  height: var(--header-height, 64px);
}

/* Logo */
.site-logo { flex-shrink: 0; }
.site-logo img { height: 36px; width: auto; }

/* Nav */
.main-nav { flex: 1; }

.main-nav__list {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  list-style: none;
  padding: 0;
}

.main-nav__link {
  display: block;
  padding: var(--space-2) var(--space-3);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-text-muted);
  text-decoration: none;
  border-radius: var(--radius-md);
  transition: color var(--transition-fast), background-color var(--transition-fast);
}

.main-nav__link:hover {
  color: var(--color-text);
  background-color: var(--color-bg-subtle);
}

.main-nav__link[aria-current="page"] {
  color: var(--color-primary);
  background-color: var(--color-primary-light);
  font-weight: var(--font-semibold);
}

/* Header CTA */
.header-cta {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-left: auto;
}

/* Mobile: hamburger toggle */
.nav-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: var(--space-2);
  background: none;
  border: none;
  cursor: pointer;
  margin-left: auto;
}

.nav-toggle__bar {
  display: block;
  width: 24px;
  height: 2px;
  background: var(--color-text);
  border-radius: 2px;
  transition: transform var(--transition-base), opacity var(--transition-base);
}

/* Mobile layout */
@media (max-width: 767px) {
  .nav-toggle { display: flex; }
  .header-cta { display: none; }

  .main-nav {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border-bottom: 1px solid var(--color-border);
    box-shadow: var(--shadow-lg);
    display: none;
    padding: var(--space-4);
  }

  /* Show when toggle is active */
  .site-header[data-nav-open="true"] .main-nav { display: block; }

  .main-nav__list {
    flex-direction: column;
    align-items: stretch;
    gap: var(--space-1);
  }

  .main-nav__link { font-size: var(--text-base); padding: var(--space-3); }

  /* Animate hamburger to X */
  .site-header[data-nav-open="true"] .nav-toggle__bar:nth-child(1) {
    transform: translateY(7px) rotate(45deg);
  }
  .site-header[data-nav-open="true"] .nav-toggle__bar:nth-child(2) {
    opacity: 0;
  }
  .site-header[data-nav-open="true"] .nav-toggle__bar:nth-child(3) {
    transform: translateY(-7px) rotate(-45deg);
  }
}
```

---

## CSS: Styling the Hero

```css
/* Hero section */
.hero {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-12);
  align-items: center;
  padding-block: var(--space-16);
  overflow: hidden;
  background: linear-gradient(
    135deg,
    var(--color-primary-light) 0%,
    white 60%
  );
}

@media (min-width: 1024px) {
  .hero {
    grid-template-columns: 1fr 1fr;
    padding-block: var(--space-20);
  }
}

.hero__content { max-width: 560px; }

.hero__eyebrow {
  display: inline-block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--color-primary);
  margin-bottom: var(--space-4);
  padding: var(--space-1) var(--space-3);
  background: var(--color-primary-light);
  border-radius: var(--radius-full);
}

.hero__heading {
  font-size: clamp(2.5rem, 5vw + 1rem, 4rem);
  font-weight: 800;
  line-height: 1.05;
  color: var(--gray-900);
  margin-bottom: var(--space-6);
}

.hero__description {
  font-size: var(--text-lg);
  color: var(--color-text-muted);
  line-height: var(--leading-relaxed);
  margin-bottom: var(--space-8);
  max-width: 48ch;
}

.hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
  margin-bottom: var(--space-4);
}

.hero__note {
  font-size: var(--text-sm);
  color: var(--color-text-muted);
}

.hero__image {
  display: none;
}

@media (min-width: 1024px) {
  .hero__image {
    display: block;
    position: relative;
  }
  .hero__image img {
    border-radius: var(--radius-xl);
    box-shadow: var(--shadow-xl);
    width: 100%;
    height: auto;
  }
}
```

---

## CSS: Styling News & Events

```css
/* News & Events section */
.news-events {
  padding-block: var(--space-16);
  background: var(--color-bg-subtle);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
}

.section-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
}

/* Card Grid */
.news-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-6);
}

@media (min-width: 768px) {
  .news-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  /* Featured card spans full width */
  .news-card--featured {
    grid-column: 1 / -1;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0;
  }
  .news-card--featured .news-card__image-link {
    border-radius: var(--radius-lg) 0 0 var(--radius-lg);
    overflow: hidden;
  }
}

@media (min-width: 1024px) {
  .news-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .news-card--featured {
    grid-column: 1 / -1;
  }
}

/* News Card */
.news-card {
  background: white;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: box-shadow var(--transition-base), transform var(--transition-base);
}

.news-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-3px);
}

.news-card__image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.news-card--featured .news-card__image {
  height: 100%;
  min-height: 280px;
}

.news-card__body {
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  flex: 1;
}

.news-card__meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
  font-size: var(--text-sm);
  color: var(--color-text-muted);
}

.news-card__title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  line-height: var(--leading-snug);
  margin-bottom: var(--space-3);
}

.news-card__title a {
  color: var(--color-text);
  text-decoration: none;
}
.news-card__title a::after {
  content: "";
  position: absolute;
  inset: 0;   /* click anywhere on card navigates */
}
.news-card { position: relative; }

.news-card__title a:hover { color: var(--color-primary); }

.news-card__excerpt {
  color: var(--color-text-muted);
  font-size: var(--text-sm);
  line-height: var(--leading-relaxed);
  flex: 1;
  margin-bottom: var(--space-4);

  /* Clamp to 3 lines */
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-card__footer {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-top: auto;
  font-size: var(--text-sm);
  color: var(--color-text-muted);
  padding-top: var(--space-4);
  border-top: 1px solid var(--color-border);
}

.news-card__author-avatar {
  border-radius: var(--radius-full);
  object-fit: cover;
  width: 28px;
  height: 28px;
  flex-shrink: 0;
}

/* Event card date badge */
.news-card--event .news-card__body {
  padding-left: calc(var(--space-6) + 60px + var(--space-4));
  position: relative;
}

.event-date-badge {
  position: absolute;
  left: var(--space-6);
  top: var(--space-6);
  width: 56px;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-md);
  text-align: center;
  padding: var(--space-2) 0;
}

.event-date-badge__month {
  display: block;
  font-size: 0.625rem;
  font-weight: var(--font-bold);
  letter-spacing: 0.1em;
  text-transform: uppercase;
  opacity: 0.85;
}

.event-date-badge__day {
  display: block;
  font-size: var(--text-2xl);
  font-weight: 800;
  line-height: 1;
}
```

---

## CSS: Styling the Footer

```css
/* layout/footer.css */

.site-footer {
  background: var(--gray-900);
  color: var(--gray-400);
  padding-top: var(--space-16);
  padding-bottom: var(--space-8);
}

/* Footer grid: 5 columns on desktop */
.footer__grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-10);
  padding-bottom: var(--space-12);
  border-bottom: 1px solid rgba(255,255,255,0.1);
  margin-bottom: var(--space-8);
}

@media (min-width: 640px) {
  .footer__grid { grid-template-columns: repeat(2, 1fr); }
}

@media (min-width: 1024px) {
  .footer__grid { grid-template-columns: 2fr 1fr 1fr 1fr 1.5fr; }
}

/* Brand column */
.footer__brand img { margin-bottom: var(--space-4); }

.footer__tagline {
  font-size: var(--text-sm);
  line-height: var(--leading-relaxed);
  max-width: 28ch;
  margin-bottom: var(--space-6);
}

.footer__social {
  display: flex;
  gap: var(--space-4);
}

.footer__social a {
  color: var(--gray-400);
  transition: color var(--transition-fast);
}
.footer__social a:hover { color: white; }

/* Nav columns */
.footer__nav-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: white;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin-bottom: var(--space-4);
}

.footer__nav ul {
  list-style: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.footer__nav a {
  color: var(--gray-400);
  text-decoration: none;
  font-size: var(--text-sm);
  transition: color var(--transition-fast);
}
.footer__nav a:hover { color: white; }

/* Contact */
.footer__contact address {
  font-style: normal;
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
  font-size: var(--text-sm);
}

.footer__contact a {
  color: var(--gray-400);
  text-decoration: none;
}
.footer__contact a:hover { color: white; text-decoration: underline; }

/* Footer bottom bar */
.footer__bottom {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-4);
  font-size: var(--text-sm);
}

.footer__legal {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4);
}

.footer__legal a {
  color: var(--gray-400);
  text-decoration: none;
}
.footer__legal a:hover { color: white; text-decoration: underline; }
```

---

## JavaScript: Mobile Navigation Toggle

```javascript
// site-header.js — minimal vanilla JS for hamburger menu
const header = document.querySelector('.site-header');
const toggle = header?.querySelector('.nav-toggle');

toggle?.addEventListener('click', () => {
  const isOpen = header.dataset.navOpen === 'true';
  header.dataset.navOpen = String(!isOpen);
  toggle.setAttribute('aria-expanded', String(!isOpen));
});

// Close nav when a link is clicked
header?.querySelectorAll('.main-nav__link').forEach(link => {
  link.addEventListener('click', () => {
    header.dataset.navOpen = 'false';
    toggle?.setAttribute('aria-expanded', 'false');
  });
});

// Close nav on Escape key
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && header.dataset.navOpen === 'true') {
    header.dataset.navOpen = 'false';
    toggle?.setAttribute('aria-expanded', 'false');
    toggle?.focus();
  }
});
```

---

## Checklist: Project Quality Gates

```
HTML
□ Valid HTML5 (no W3C errors)
□ Semantic structure (header, main, footer, article, section, nav)
□ One <h1> per page
□ All images have alt attributes
□ All images have width and height
□ All interactive elements keyboard-accessible
□ All form inputs have labels
□ aria-current="page" on active nav link
□ Skip navigation link present
□ No inline styles (except dynamic values)
□ CSRF protection on all forms

CSS
□ box-sizing: border-box applied globally
□ Normalize.css loaded first
□ Design tokens defined as CSS custom properties
□ No !important in component CSS
□ Specificity kept low and flat
□ :focus-visible styles on all interactive elements
□ Mobile-first media queries
□ No layout issues at 320px viewport width
□ No layout issues at 1440px viewport width
□ Reduced motion respected
□ Print styles (at minimum, hide nav and footer)
□ No horizontal scroll on mobile

Performance
□ Hero image has fetchpriority="high"
□ Below-fold images have loading="lazy"
□ Fonts use font-display: swap
□ CSS served with cache headers (1 year)
□ CSS minified in production

Accessibility (WCAG 2.1 AA)
□ Colour contrast ratio ≥ 4.5:1 for normal text
□ Colour contrast ratio ≥ 3:1 for large text and UI components
□ No colour alone conveys meaning
□ Focus visible on all interactive elements
□ No keyboard traps
□ Page language set on <html>
□ Error messages linked to inputs with aria-describedby
□ Icon buttons have aria-label
```

---

## Key Takeaways from the Full Project

- Structure HTML semantically first; CSS is applied on top
- A sticky header uses `position: sticky; top: 0; z-index`
- Hero layout uses CSS Grid for two-column at desktop, stacked at mobile
- Card grids use `auto-fill` or `auto-fit` with `minmax()` for responsive behaviour
- Clickable card pattern: `position: relative` on card + `::after` with `position: absolute; inset: 0` on the link
- Footer uses CSS Grid with responsive column counts
- Mobile navigation requires minimal JavaScript — primarily toggling a `data-*` attribute
- Always test at 320px (smallest common mobile) and 1440px (common desktop)
