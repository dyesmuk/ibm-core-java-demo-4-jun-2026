# Module 09 — Tables

## 9.1 When to Use Tables

HTML tables are for **tabular data** — information that has a natural row/column relationship. They are **not** for layout. CSS Grid and Flexbox replaced layout tables in the early 2000s; using tables for layout today is a serious error.

**Appropriate uses:**
- Financial reports, invoices
- Comparison matrices
- Schedules / timetables
- Data export displays
- System metrics dashboards

**Inappropriate uses:**
- Page layout (header, sidebar, footer)
- Form alignment
- Navigation menus
- Any non-tabular content

---

## 9.2 Basic Table Structure

```html
<table>
  <thead>
    <tr>
      <th scope="col">Invoice #</th>
      <th scope="col">Client</th>
      <th scope="col">Date</th>
      <th scope="col">Amount</th>
      <th scope="col">Status</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>INV-2024-001</td>
      <td>Tata Consultancy Services</td>
      <td><time datetime="2024-11-01">01 Nov 2024</time></td>
      <td>₹1,25,000</td>
      <td><span class="badge badge-success">Paid</span></td>
    </tr>
    <tr>
      <td>INV-2024-002</td>
      <td>Infosys Limited</td>
      <td><time datetime="2024-11-15">15 Nov 2024</time></td>
      <td>₹87,500</td>
      <td><span class="badge badge-warning">Pending</span></td>
    </tr>
  </tbody>
  <tfoot>
    <tr>
      <td colspan="3"><strong>Total</strong></td>
      <td><strong>₹2,12,500</strong></td>
      <td></td>
    </tr>
  </tfoot>
</table>
```

### Table Element Reference

| Element | Purpose |
|---------|---------|
| `<table>` | Wrapper element |
| `<thead>` | Header row group — not scrolled with body |
| `<tbody>` | Body row group — repeatable for long tables |
| `<tfoot>` | Footer row group — totals, summaries |
| `<tr>` | Table row |
| `<th>` | Header cell (bold, centered by default) |
| `<td>` | Data cell |
| `<caption>` | Table caption / title |
| `<colgroup>` / `<col>` | Column-level styling |

---

## 9.3 Table Accessibility

Tables are complex for screen readers. Proper markup is essential.

### `scope` Attribute

Tells screen readers which cells a header describes:

```html
<table>
  <thead>
    <tr>
      <th scope="col">Employee</th>
      <th scope="col">Department</th>
      <th scope="col">Salary</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th scope="row">Jane Smith</th>  <!-- Row header -->
      <td>Engineering</td>
      <td>₹18,00,000</td>
    </tr>
  </tbody>
</table>
```

- `scope="col"`: This `<th>` is a header for its column
- `scope="row"`: This `<th>` is a header for its row
- `scope="colgroup"`: Header for a group of columns
- `scope="rowgroup"`: Header for a group of rows

### `<caption>` — Table Title

```html
<table>
  <caption>
    Employee Salary Report — Q3 2024
    <small>(Amounts in Indian Rupees, annual CTC)</small>
  </caption>
  <thead>...</thead>
  <tbody>...</tbody>
</table>
```

Screen readers announce the caption before reading the table. It is also useful as a visible title. Style it with CSS (`caption-side: bottom` to position below).

### Complex Tables: `id` and `headers`

For tables with multiple levels of headers (complex data grids):

```html
<table>
  <thead>
    <tr>
      <th id="hdr-region" colspan="2">Region</th>
      <th id="hdr-q1" colspan="2">Q1</th>
      <th id="hdr-q2" colspan="2">Q2</th>
    </tr>
    <tr>
      <th id="hdr-north">North</th>
      <th id="hdr-south">South</th>
      <th id="hdr-q1-online">Online</th>
      <th id="hdr-q1-offline">Offline</th>
      <th id="hdr-q2-online">Online</th>
      <th id="hdr-q2-offline">Offline</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td headers="hdr-region hdr-north">₹45L</td>
      <td headers="hdr-region hdr-south">₹38L</td>
      <!-- ... -->
    </tr>
  </tbody>
</table>
```

---

## 9.4 Spanning Cells

```html
<!-- colspan: cell spans multiple columns -->
<tr>
  <td colspan="2">Merged across 2 columns</td>
  <td>Regular cell</td>
</tr>

<!-- rowspan: cell spans multiple rows -->
<tr>
  <th rowspan="3">Q1</th>
  <td>January</td>
  <td>₹12L</td>
</tr>
<tr>
  <td>February</td>
  <td>₹14L</td>
</tr>
<tr>
  <td>March</td>
  <td>₹11L</td>
</tr>
```

---

## 9.5 Column Styling with `<colgroup>`

Apply styles to entire columns without repeating on each cell:

```html
<table>
  <colgroup>
    <col class="col-id" style="width: 80px;">
    <col class="col-name">
    <col class="col-date" style="width: 120px;">
    <col class="col-amount" style="width: 120px; text-align: right;">
    <col class="col-status" style="width: 100px;">
  </colgroup>
  <thead>...</thead>
  <tbody>...</tbody>
</table>
```

> CSS only supports a limited set of properties on `<col>`: `width`, `border`, `background`, `visibility`. For other properties, style `<td>` and `<th>` directly.

---

## 9.6 Responsive Tables

Wide tables overflow on mobile. Several techniques exist:

### Horizontal Scroll Wrapper

```html
<div class="table-wrapper" role="region" aria-label="Invoice data table" tabindex="0">
  <table>
    ...
  </table>
</div>
```

```css
.table-wrapper {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}
```

`tabindex="0"` makes the wrapper keyboard-focusable so keyboard users can scroll it. `role="region"` + `aria-label` tells screen readers about the scrollable container.

### Priority Columns (Hide on mobile)

```css
@media (max-width: 600px) {
  .col-optional {
    display: none;
  }
}
```

---

## 9.7 Tables in Thymeleaf

```html
<table>
  <caption th:text="'Invoice Report — ' + ${reportPeriod}">Invoice Report</caption>
  <thead>
    <tr>
      <th scope="col">Invoice #</th>
      <th scope="col">Client</th>
      <th scope="col">Amount</th>
      <th scope="col">Status</th>
      <th scope="col"><span class="sr-only">Actions</span></th>
    </tr>
  </thead>
  <tbody>
    <!-- Empty state -->
    <tr th:if="${#lists.isEmpty(invoices)}">
      <td colspan="5" class="empty-state">No invoices found.</td>
    </tr>
    <!-- Data rows -->
    <tr th:each="invoice : ${invoices}" 
        th:classappend="${invoice.overdue} ? 'row-overdue' : ''">
      <td th:text="${invoice.number}">INV-001</td>
      <td th:text="${invoice.clientName}">Client Name</td>
      <td th:text="${#numbers.formatCurrency(invoice.amount)}">₹0</td>
      <td>
        <span th:text="${invoice.status}" 
              th:classappend="'badge-' + ${invoice.statusClass}">Status</span>
      </td>
      <td>
        <a th:href="@{/invoices/{id}(id=${invoice.id})}" class="btn btn-sm">View</a>
        <a th:href="@{/invoices/{id}/edit(id=${invoice.id})}" class="btn btn-sm">Edit</a>
      </td>
    </tr>
  </tbody>
  <tfoot>
    <tr>
      <td colspan="2"><strong>Total</strong></td>
      <td th:text="${#numbers.formatCurrency(totalAmount)}">₹0</td>
      <td colspan="2"></td>
    </tr>
  </tfoot>
</table>
```

---

## 9.8 Sortable Tables

For enterprise data grids, indicate sortable columns:

```html
<thead>
  <tr>
    <th scope="col">
      <a href="?sort=name&dir=asc" 
         aria-sort="none"
         class="sortable">
        Client Name
        <span aria-hidden="true" class="sort-icon">↕</span>
      </a>
    </th>
    <th scope="col" aria-sort="descending">
      <a href="?sort=date&dir=asc" class="sortable">
        Date
        <span aria-hidden="true" class="sort-icon sort-desc">↓</span>
      </a>
    </th>
  </tr>
</thead>
```

`aria-sort` values: `none`, `ascending`, `descending`, `other`

---

## Key Takeaways

- Tables are for tabular data, never for layout.
- Use `<thead>`, `<tbody>`, `<tfoot>` for structure; always add `<caption>`.
- Use `scope="col"` on column headers and `scope="row"` on row headers.
- Wrap wide tables in a scrollable container for mobile.
- In Thymeleaf, handle the empty state with `th:if="${#lists.isEmpty(...)}"`.

---

## Self-Check Questions

1. Why should you never use tables for page layout?
2. What is the `scope` attribute and why is it important for accessibility?
3. How do you merge cells across two columns? How about across two rows?
4. What is the accessible way to make a wide table work on mobile?
5. How would you highlight rows that are overdue in a Thymeleaf table?
