# 🍽️ MongoDB Restaurant Queries — Practice Exercise

> **Scenario:** You've been handed a dataset of NYC restaurants. Your job: slice, dice, and query your way through it like a seasoned data chef. 🧑‍🍳

---

## ⚙️ Setup

1. Download the dataset → [restaurants.json](https://drive.google.com/file/d/1JvQsGXFIu5tZ3uIfMSrQWk8UihOhY6pS/view?usp=sharing)
2. Import it into your local MongoDB server:
   ```bash
   mongoimport --db nyc --collection restaurants --file restaurants.json --jsonArray
   ```
3. Open your MongoDB shell and switch to the database:
   ```js
   use nyc
   ```

---

## 🗂️ Document Structure

Here's what a single restaurant document looks like — get familiar with it!

```json
{
  "address": {
    "building": "1007",
    "coord": [ -73.856077, 40.848447 ],
    "street": "Morris Park Ave",
    "zipcode": "10462"
  },
  "borough": "Bronx",
  "cuisine": "Bakery",
  "grades": [
    { "date": { "$date": 1393804800000 }, "grade": "A", "score": 2 },
    { "date": { "$date": 1378857600000 }, "grade": "A", "score": 6 },
    { "date": { "$date": 1358985600000 }, "grade": "A", "score": 10 },
    { "date": { "$date": 1322006400000 }, "grade": "A", "score": 9 },
    { "date": { "$date": 1299715200000 }, "grade": "B", "score": 14 }
  ],
  "name": "Morris Park Bake Shop",
  "restaurant_id": "30075445"
}
```

---

## 📋 Queries

### 🔍 Basic Retrieval

**Q1.** Display **all documents** in the `restaurants` collection.

**Q2.** Display only `restaurant_id`, `name`, `borough`, and `cuisine` for all restaurants.

**Q3.** Same as above, but **exclude `_id`** from the results.

**Q4.** Display `restaurant_id`, `name`, `borough`, and `zipcode` — again without `_id`.

---

### 📍 Filtering by Location & Borough

**Q5.** Find all restaurants located in the **Bronx**.

**Q6.** Show only the **first 5** restaurants in the Bronx.

**Q7.** Skip the first 5 Bronx restaurants and show the **next 5**.

**Q10.** Find restaurants with a latitude value **less than -95.754168**.

---

### 🏆 Score-Based Filtering

**Q8.** Find restaurants that achieved a score **greater than 90**.

> 💡 **Two approaches:**
>
> *At least one grade over 90:*
> ```js
> db.restaurants.find({ grades: { $elemMatch: { "score": { $gt: 90 } } } });
> ```
> *All grades over 90:*
> ```js
> db.restaurants.find({ grades: { $all: [{ score: { $gt: 90 } }] } });
> ```

**Q9.** Find restaurants with a score **between 80 and 100** (exclusive).

**Q20.** Find `restaurant_id`, `name`, `borough`, and `cuisine` for restaurants with a score **not more than 10**.

**Q30.** Find `restaurant_id`, `name`, and `grades` for restaurants where the score is **divisible by 7** (remainder = 0).

---

### 🌍 Geo + Cuisine Filtering

**Q11.** Find restaurants that:
- Do **not** serve American cuisine
- Have a score **greater than 70**
- Are located at a latitude **less than -65.754168**

**Q12.** Same as Q11, but **without using the `$and` operator**.

**Q17.** Find restaurants in the **Bronx** that serve either **American** or **Chinese** cuisine.

---

### 🗺️ Borough Filtering

**Q18.** Display `restaurant_id`, `name`, `borough`, and `cuisine` for restaurants in **Staten Island, Queens, Bronx, or Brooklyn**.

**Q19.** Display the same fields for restaurants **not** in those four boroughs.

---

### 🔤 Name Pattern Matching (Regex)

**Q14.** Find `restaurant_id`, `name`, `borough`, and `cuisine` for restaurants whose name **starts with `'Wil'`**.

**Q15.** Same fields for restaurants whose name **ends with `'ces'`**.

**Q16.** Same fields for restaurants whose name **contains `'Reg'`** anywhere.

**Q31.** Find `name`, `borough`, `longitude`, `latitude`, and `cuisine` for restaurants with **`'mon'` anywhere** in the name.

**Q32.** Same fields for restaurants whose name **begins with `'Mad'`**.

---

### 🎯 Combined & Complex Conditions

**Q13.** Find restaurants that:
- Do **not** serve American cuisine
- Have a grade of **'A'**
- Are **not** in Brooklyn

Sort results by **cuisine in descending order**.

**Q21.** Find `restaurant_id`, `name`, `borough`, and `cuisine` for restaurants that:
- Serve neither **American** nor **Chinese** cuisine, **OR**
- Have a name starting with **'Wil'**

---

### 📅 Array & Date Queries

**Q22.** Find `restaurant_id`, `name`, and `grades` for restaurants that received:
- Grade **"A"**
- Score **11**
- On **`2014-08-11T00:00:00Z`**

**Q23.** Find `restaurant_id`, `name`, and `grades` where the **2nd element** of the grades array has:
- Grade **"A"**
- Score **9**
- Date **`2014-08-11T00:00:00Z`**

**Q24.** Find `restaurant_id`, `name`, `address`, and geo-location for restaurants where the **2nd element of the `coord` array** is **more than 42 and up to 52**.

---

### 📊 Sorting

**Q25.** List all restaurants sorted by **name ascending**.

**Q26.** List all restaurants sorted by **name descending**.

**Q27.** Sort by **cuisine ascending**, and within the same cuisine sort by **borough descending**.

---

### 🛠️ Miscellaneous

**Q28.** Check whether **all address documents** contain a `street` field.

**Q29.** Select all documents where the `coord` field value is of type **Double**.

---

---

## 🔬 Aggregation Pipeline Challenges

> **Level up!** These questions require the MongoDB Aggregation Pipeline. Think in stages: each `$stage` feeds into the next. 🧪

---

### 📦 Grouping & Counting

**A1.** Count the **total number of restaurants in each borough**. Sort results by count in descending order.

**A2.** Count the **total number of restaurants for each cuisine type**. Show only cuisines with more than 10 restaurants.

**A3.** For each **borough**, find the **number of distinct cuisines** available.

**A4.** Find the **top 3 cuisines** by total restaurant count across the entire dataset.

---

### 📈 Averages & Score Analysis

**A5.** Calculate the **average score** for each borough (across all grades of all restaurants). Round to 2 decimal places.

**A6.** Find the **cuisine type with the highest average score**. Only consider cuisines with at least 5 restaurants.

**A7.** For each restaurant, calculate its **own average score** across all its grade entries. Display the restaurant name, borough, and computed average — sorted by average score descending.

**A8.** Find the **top 5 boroughs** ranked by their overall average restaurant score.

---

### 🏅 Rankings & Top-N

**A9.** For each **borough**, find the **restaurant with the single highest score** ever recorded in any grade entry.

**A10.** List the **top 10 restaurants by average score** (computed from their grades array). Show name, borough, cuisine, and average score.

**A11.** For each borough, find the **most common cuisine** (the cuisine with the highest restaurant count in that borough).

---

### 🔎 Unwinding & Array Operations

**A12.** Use `$unwind` on the `grades` array and find the **total number of 'A', 'B', and 'C' grades** handed out across all restaurants. Group by grade letter.

**A13.** After unwinding grades, find the **month-wise distribution of inspections** (how many inspections happened in each month across all years). Sort by month number.

**A14.** Find restaurants that have received **at least one 'C' grade**. Display name, borough, cuisine, and the total count of 'C' grades for each.

---

### 🧩 Advanced: $facet & Multi-Dimensional

**A15.** Use `$facet` to produce a **single aggregation result** that simultaneously returns:
- The count of restaurants per borough
- The count of restaurants per cuisine (top 5 only)
- The overall average score across the entire dataset

> 💡 *Hint: `$facet` lets you run multiple independent aggregation pipelines in a single stage and get all results in one document.*

---

## ✅ Tips & Reminders

**For `find()` queries:**
- Use **projection** (second argument in `find()`) to include/exclude fields.
- Use **`$elemMatch`** when filtering inside arrays on multiple conditions.
- Use **dot notation** (e.g., `"grades.score"`, `"address.coord"`) to access nested fields.
- Use **`$regex`** for pattern matching on string fields.
- Use **`.sort()`**, **`.limit()`**, and **`.skip()`** for ordering and pagination.

**For aggregation pipelines:**
- Always think in **stages** — the output of one `$stage` is the input to the next.
- Use **`$unwind`** to flatten array fields (like `grades`) before grouping on them.
- Use **`$group`** with accumulators like `$sum`, `$avg`, `$max`, `$min`, `$push`.
- Use **`$project`** inside a pipeline to reshape documents or compute new fields.
- Use **`$facet`** to run multiple sub-pipelines in parallel and get results in one go.
- Use **`$round`** inside `$project` to format decimal values.
- Use **`$sort` + `$limit`** together for efficient Top-N queries.

---

*Happy querying! 🚀*
