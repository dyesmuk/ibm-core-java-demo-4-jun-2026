# Module 08 — Images & Media

## 8.1 The `<img>` Element

```html
<img src="/images/product-hero.jpg" 
     alt="Widget Pro 3000 on a white background" 
     width="800" 
     height="600">
```

### Required Attributes

| Attribute | Purpose | Notes |
|-----------|---------|-------|
| `src` | URL of the image | Can be absolute, root-relative, or data URI |
| `alt` | Alternative text | **Required for accessibility and SEO** |

### Recommended Attributes

| Attribute | Purpose |
|-----------|---------|
| `width` | Natural width in pixels |
| `height` | Natural height in pixels |
| `loading` | `lazy` or `eager` (default) |
| `decoding` | `async`, `sync`, or `auto` |
| `fetchpriority` | `high`, `low`, or `auto` |

---

## 8.2 The `alt` Attribute — Critical for Accessibility

`alt` text is displayed when the image fails to load and is read by screen readers.

### Writing Good Alt Text

| Image Type | Alt Text Strategy | Example |
|------------|-------------------|---------|
| Meaningful photo | Describe what matters | `"CEO Jane Smith presenting at DevSummit 2024"` |
| Product image | Describe the product | `"Blue Widget Pro 3000, stainless steel finish"` |
| Chart/graph | Describe the data | `"Bar chart showing Q3 revenue: ₹12Cr, up 15% from Q2"` |
| Decorative | Empty string `alt=""` | Tells screen readers to skip it |
| Logo | Company name | `"Acme Corp"` |
| Icon with adjacent text | Empty `alt=""` | Text already explains it |

```html
<!-- ❌ Bad alt text -->
<img src="chart.png" alt="chart">
<img src="icon-download.svg" alt="download icon">  <!-- if button says "Download" -->

<!-- ✅ Good alt text -->
<img src="chart.png" alt="Revenue growth chart: ₹8Cr in Q1, ₹10Cr in Q2, ₹12Cr in Q3 2024">
<img src="icon-download.svg" alt="">  <!-- empty: the button text "Download Report" is enough -->
```

### When `alt=""` is Correct

An empty `alt` attribute (not missing — explicitly empty) tells screen readers to ignore the image entirely. Use it for:
- Decorative images
- Icons that duplicate adjacent text
- Spacer images
- Background image patterns represented in HTML (though CSS background-image is better)

---

## 8.3 Image Dimensions — Preventing Layout Shift

Always specify `width` and `height` to prevent **Cumulative Layout Shift (CLS)** — one of Google's Core Web Vitals:

```html
<!-- ❌ No dimensions: browser doesn't know how much space to reserve -->
<img src="/images/hero.jpg" alt="Hero image">

<!-- ✅ With dimensions: browser reserves space before image loads -->
<img src="/images/hero.jpg" alt="Hero image" width="1200" height="630">
```

The browser uses `width` and `height` to calculate **aspect ratio** before the image loads. With CSS `max-width: 100%`, this still works responsively:

```css
img {
  max-width: 100%;
  height: auto;  /* maintains aspect ratio */
}
```

---

## 8.4 Responsive Images

### `srcset` — Resolution Switching

Serve different image sizes based on device pixel density and viewport width:

```html
<!-- Density descriptor: 1x for standard, 2x for retina displays -->
<img src="/images/logo.png" 
     srcset="/images/logo.png 1x, /images/logo@2x.png 2x"
     alt="Acme Corp" 
     width="200" height="60">

<!-- Width descriptor with sizes: more control -->
<img src="/images/hero-800.jpg"
     srcset="/images/hero-400.jpg 400w,
             /images/hero-800.jpg 800w,
             /images/hero-1200.jpg 1200w,
             /images/hero-1600.jpg 1600w"
     sizes="(max-width: 600px) 100vw,
            (max-width: 1200px) 50vw,
            800px"
     alt="Aerial view of Bengaluru tech park"
     width="1200" height="630">
```

- `srcset` lists available image files and their widths
- `sizes` tells the browser which size the image will display at (for the current viewport)
- The browser picks the best image — no JavaScript required

### `<picture>` — Art Direction and Format Switching

```html
<picture>
  <!-- Modern format for browsers that support it -->
  <source type="image/avif" srcset="/images/hero.avif">
  <source type="image/webp" srcset="/images/hero.webp">
  <!-- Fallback for older browsers -->
  <img src="/images/hero.jpg" 
       alt="Team photo" 
       width="1200" height="630">
</picture>
```

```html
<!-- Art direction: different crop for mobile -->
<picture>
  <source media="(max-width: 600px)" srcset="/images/hero-portrait.jpg">
  <source media="(min-width: 601px)" srcset="/images/hero-landscape.jpg">
  <img src="/images/hero-landscape.jpg" alt="Team photo" width="1200" height="630">
</picture>
```

### Image Format Guide

| Format | Use Case | Browser Support |
|--------|----------|----------------|
| **AVIF** | Best compression, modern projects | Chrome, Firefox, Safari 16+ |
| **WebP** | Good compression, wide support | All modern browsers |
| **SVG** | Icons, logos, illustrations (vector) | All browsers |
| **PNG** | Screenshots, images needing transparency | All browsers |
| **JPEG** | Photos without transparency | All browsers |
| **GIF** | Simple animations (consider CSS/video) | All browsers |

---

## 8.5 Lazy Loading

Defer loading images not visible in the initial viewport:

```html
<!-- Native lazy loading (supported in all modern browsers) -->
<img src="/images/product-thumbnail.jpg" 
     alt="Widget Pro" 
     loading="lazy"
     width="300" height="200">

<!-- Hero image: always load eagerly (it IS in the viewport) -->
<img src="/images/hero.jpg" 
     alt="Hero" 
     loading="eager"
     fetchpriority="high"
     width="1200" height="630">
```

**Rules:**
- `loading="lazy"`: Use for images below the fold (not visible on first load)
- `loading="eager"` (default): Use for LCP (Largest Contentful Paint) images
- Never lazy-load the hero/LCP image — it delays the most important content

---

## 8.6 SVG Images

SVG (Scalable Vector Graphics) are ideal for logos, icons, and illustrations:

```html
<!-- As <img> tag: simple, accessible, cached -->
<img src="/images/logo.svg" alt="Acme Corp" width="120" height="40">

<!-- Inline SVG: can be styled with CSS, animated, and has better performance for icons -->
<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" 
     viewBox="0 0 24 24" 
     aria-hidden="true"   <!-- decorative, text label elsewhere -->
     focusable="false">
  <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
</svg>
<span>Layers</span>

<!-- Standalone meaningful SVG needs a title -->
<svg xmlns="http://www.w3.org/2000/svg" 
     role="img" 
     aria-labelledby="svg-title">
  <title id="svg-title">Warning: unsaved changes</title>
  <!-- paths -->
</svg>
```

---

## 8.7 Video and Audio

### `<video>`

```html
<video 
  width="1280" height="720"
  controls
  preload="metadata"
  poster="/images/video-poster.jpg">
  <source src="/videos/product-demo.webm" type="video/webm">
  <source src="/videos/product-demo.mp4" type="video/mp4">
  <track src="/captions/demo-en.vtt" kind="subtitles" srclang="en" label="English" default>
  <track src="/captions/demo-hi.vtt" kind="subtitles" srclang="hi" label="Hindi">
  <p>Your browser doesn't support HTML video. 
     <a href="/videos/product-demo.mp4">Download the video</a>.</p>
</video>
```

| Attribute | Purpose |
|-----------|---------|
| `controls` | Show play/pause/volume controls |
| `autoplay` | Autoplay (combine with `muted` or browsers block it) |
| `muted` | Mute by default (required for autoplay) |
| `loop` | Loop the video |
| `preload` | `none`, `metadata`, `auto` |
| `poster` | Image shown before play |

### `<track>` for Accessibility

Captions (`<track kind="subtitles">`) are legally required in many enterprise contexts (ADA, WCAG). Always provide captions for video content.

### `<audio>`

```html
<audio controls preload="metadata">
  <source src="/audio/notification.ogg" type="audio/ogg">
  <source src="/audio/notification.mp3" type="audio/mpeg">
  <p>Your browser doesn't support HTML audio.</p>
</audio>
```

---

## 8.8 Images in Thymeleaf

```html
<!-- Static image in static folder -->
<img th:src="@{/images/logo.svg}" alt="Company Logo" width="120" height="40">

<!-- Dynamic image URL from model -->
<img th:src="${product.imageUrl}" 
     th:alt="${product.name + ' product image'}"
     width="300" height="300"
     loading="lazy">

<!-- Conditional image with fallback -->
<img th:src="${user.avatarUrl != null} ? ${user.avatarUrl} : @{/images/default-avatar.png}"
     th:alt="${user.displayName}"
     width="64" height="64">

<!-- Serve image from controller (e.g., from database blob) -->
<img th:src="@{/api/users/{id}/avatar(id=${user.id})}"
     th:alt="${user.name}"
     width="64" height="64"
     loading="lazy">
```

---

## Key Takeaways

- `alt` text is mandatory — empty `alt=""` for decorative images, descriptive text for meaningful images.
- Always specify `width` and `height` to prevent layout shift (CLS).
- Use `srcset` and `<picture>` for responsive images; serve AVIF/WebP with JPEG fallback.
- Use `loading="lazy"` for below-fold images; never lazy-load the hero image.
- Provide `<track>` captions for all video content.
- Use Thymeleaf's `@{}` for all image source URLs.

---

## Self-Check Questions

1. What is Cumulative Layout Shift (CLS) and how does specifying `width` and `height` prevent it?
2. When should `alt=""` (empty) be used instead of descriptive alt text?
3. What is the difference between `srcset` with `w` descriptors and `srcset` with `x` descriptors?
4. How would you serve an avatar image stored in a database from a Spring Boot controller and reference it in a Thymeleaf template?
5. Why should you never lazy-load the hero image?
