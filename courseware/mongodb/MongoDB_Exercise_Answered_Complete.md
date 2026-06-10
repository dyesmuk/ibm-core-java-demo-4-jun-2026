# 🍽️ MongoDB Restaurant Queries — Practice Exercise (Answered)

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

## 📋 Queries & Answers

### 🔍 Basic Retrieval

**Q1.** Display **all documents** in the `restaurants` collection.

```js
db.restaurants.find();
```

**Q2.** Display only `restaurant_id`, `name`, `borough`, and `cuisine` for all restaurants.

```js
db.restaurants.find({}, { restaurant_id: 1, name: 1, borough: 1, cuisine: 1 });
```

**Q3.** Same as above, but **exclude `_id`** from the results.

```js
db.restaurants.find({}, { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 });
```

**Q4.** Display `restaurant_id`, `name`, `borough`, and `zipcode` — again without `_id`.

```js
db.restaurants.find({}, { restaurant_id: 1, name: 1, borough: 1, "address.zipcode": 1, _id: 0 });
```

---

### 📍 Filtering by Location & Borough

**Q5.** Find all restaurants located in the **Bronx**.

```js
db.restaurants.find({ borough: "Bronx" });
```

**Q6.** Show only the **first 5** restaurants in the Bronx.

```js
db.restaurants.find({ borough: "Bronx" }).limit(5);
```

**Q7.** Skip the first 5 Bronx restaurants and show the **next 5**.

```js
db.restaurants.find({ borough: "Bronx" }).skip(5).limit(5);
```

**Q10.** Find restaurants with a latitude value **less than -95.754168**.

```js
db.restaurants.find({ "address.coord": { $lt: -95.754168 } });
```

---

### 🏆 Score-Based Filtering

**Q8.** Find restaurants that achieved a score **greater than 90**.

> 💡 **Two approaches:**
>
> *At least one grade over 90:*
> ```js
> db.restaurants.find({ grades: { $elemMatch: { score: { $gt: 90 } } } });
> ```
> *All grades over 90:*
> ```js
> db.restaurants.find({ grades: { $all: [{ score: { $gt: 90 } }] } });
> ```

**Q9.** Find restaurants with a score **between 80 and 100** (exclusive).

```js
db.restaurants.find({ grades: { $elemMatch: { score: { $gt: 80, $lt: 100 } } } });
```

**Q20.** Find `restaurant_id`, `name`, `borough`, and `cuisine` for restaurants with a score **not more than 10**.

```js
db.restaurants.find(
  { "grades.score": { $lte: 10 } },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q30.** Find `restaurant_id`, `name`, and `grades` for restaurants where the score is **divisible by 7**.

```js
db.restaurants.find(
  { "grades.score": { $mod: [7, 0] } },
  { restaurant_id: 1, name: 1, grades: 1, _id: 0 }
);
```

---

### 🌍 Geo + Cuisine Filtering

**Q11.** Find restaurants that do not serve American cuisine, have a score > 70, and latitude < -65.754168.

```js
db.restaurants.find({
  $and: [
    { cuisine: { $ne: "American " } },
    { "grades.score": { $gt: 70 } },
    { "address.coord": { $lt: -65.754168 } }
  ]
});
```

**Q12.** Same as Q11, but **without using `$and`**.

```js
db.restaurants.find({
  cuisine: { $ne: "American " },
  "grades.score": { $gt: 70 },
  "address.coord": { $lt: -65.754168 }
});
```

**Q17.** Find restaurants in the **Bronx** that serve either **American** or **Chinese** cuisine.

```js
db.restaurants.find({
  borough: "Bronx",
  $or: [{ cuisine: "American " }, { cuisine: "Chinese" }]
});
```

---

### 🗺️ Borough Filtering

**Q18.** Display `restaurant_id`, `name`, `borough`, and `cuisine` for restaurants in **Staten Island, Queens, Bronx, or Brooklyn**.

```js
db.restaurants.find(
  { borough: { $in: ["Staten Island", "Queens", "Bronx", "Brooklyn"] } },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q19.** Display the same fields for restaurants **not** in those four boroughs.

```js
db.restaurants.find(
  { borough: { $nin: ["Staten Island", "Queens", "Bronx", "Brooklyn"] } },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

---

### 🔤 Name Pattern Matching (Regex)

**Q14.** Restaurants whose name **starts with `'Wil'`**.

```js
db.restaurants.find(
  { name: /^Wil/ },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q15.** Restaurants whose name **ends with `'ces'`**.

```js
db.restaurants.find(
  { name: /ces$/ },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q16.** Restaurants whose name **contains `'Reg'`** anywhere.

```js
db.restaurants.find(
  { name: /Reg/ },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q31.** Restaurants with **`'mon'`** anywhere in the name — show name, borough, coordinates, and cuisine.

```js
db.restaurants.find(
  { name: /mon/i },
  { name: 1, borough: 1, cuisine: 1, "address.coord": 1, _id: 0 }
);
```

**Q32.** Restaurants whose name **begins with `'Mad'`**.

```js
db.restaurants.find(
  { name: /^Mad/ },
  { name: 1, borough: 1, cuisine: 1, "address.coord": 1, _id: 0 }
);
```

---

### 🎯 Combined & Complex Conditions

**Q13.** Non-American cuisine, grade 'A', not in Brooklyn — sorted by cuisine descending.

```js
db.restaurants.find({
  cuisine: { $ne: "American " },
  "grades.grade": "A",
  borough: { $ne: "Brooklyn" }
}).sort({ cuisine: -1 });
```

**Q21.** Serve neither American nor Chinese cuisine **OR** name starts with 'Wil'.

```js
db.restaurants.find(
  {
    $or: [
      { cuisine: { $nin: ["American ", "Chinese"] } },
      { name: /^Wil/ }
    ]
  },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

---

### 📅 Array & Date Queries

**Q22.** Grade **"A"**, score **11**, on **`2014-08-11T00:00:00Z`**.

```js
db.restaurants.find(
  {
    grades: {
      $elemMatch: {
        grade: "A",
        score: 11,
        date: ISODate("2014-08-11T00:00:00Z")
      }
    }
  },
  { restaurant_id: 1, name: 1, grades: 1, _id: 0 }
);
```

**Q23.** **2nd element** of grades array has grade "A", score 9, date `2014-08-11T00:00:00Z`.

```js
db.restaurants.find(
  {
    "grades.1.grade": "A",
    "grades.1.score": 9,
    "grades.1.date": ISODate("2014-08-11T00:00:00Z")
  },
  { restaurant_id: 1, name: 1, grades: 1, _id: 0 }
);
```

**Q24.** **2nd element of `coord`** is more than 42 and up to 52.

```js
db.restaurants.find(
  { "address.coord.1": { $gt: 42, $lte: 52 } },
  { restaurant_id: 1, name: 1, address: 1, _id: 0 }
);
```

---

### 📊 Sorting

**Q25.** Sort by **name ascending**.

```js
db.restaurants.find().sort({ name: 1 });
```

**Q26.** Sort by **name descending**.

```js
db.restaurants.find().sort({ name: -1 });
```

**Q27.** Sort by **cuisine ascending**, then **borough descending**.

```js
db.restaurants.find().sort({ cuisine: 1, borough: -1 });
```

---

### 🛠️ Miscellaneous

**Q28.** Check whether all address documents contain a `street` field.

```js
// Returns documents where 'street' is MISSING — if no results, all have it
db.restaurants.find({ "address.street": { $exists: false } });
```

**Q29.** Select all documents where the `coord` field value is of type **Double**.

```js
db.restaurants.find({ "address.coord": { $type: 1 } });
```

---

## 🔬 Aggregation Pipeline Challenges

> **Level up!** These questions require the MongoDB Aggregation Pipeline. Think in stages: each `$stage` feeds into the next. 🧪

---

### 📦 Grouping & Counting

**A1.** Count the **total number of restaurants in each borough**, sorted by count descending.

```js
db.restaurants.aggregate([
  { $group: { _id: "$borough", total: { $sum: 1 } } },
  { $sort: { total: -1 } }
]);
```

**A2.** Count restaurants per **cuisine type** — show only cuisines with more than 10 restaurants.

```js
db.restaurants.aggregate([
  { $group: { _id: "$cuisine", total: { $sum: 1 } } },
  { $match: { total: { $gt: 10 } } },
  { $sort: { total: -1 } }
]);
```

**A3.** For each **borough**, find the **number of distinct cuisines** available.

```js
db.restaurants.aggregate([
  { $group: { _id: { borough: "$borough", cuisine: "$cuisine" } } },
  { $group: { _id: "$_id.borough", distinctCuisines: { $sum: 1 } } },
  { $sort: { distinctCuisines: -1 } }
]);
```

**A4.** Find the **top 3 cuisines** by total restaurant count.

```js
db.restaurants.aggregate([
  { $group: { _id: "$cuisine", total: { $sum: 1 } } },
  { $sort: { total: -1 } },
  { $limit: 3 }
]);
```

---

### 📈 Averages & Score Analysis

**A5.** Calculate the **average score per borough** across all grades of all restaurants. Round to 2 decimal places.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  {
    $group: {
      _id: "$borough",
      avgScore: { $avg: "$grades.score" }
    }
  },
  {
    $project: {
      _id: 1,
      avgScore: { $round: ["$avgScore", 2] }
    }
  },
  { $sort: { avgScore: -1 } }
]);
```

**A6.** Find the **cuisine with the highest average score** — only consider cuisines with at least 5 restaurants.

```js
db.restaurants.aggregate([
  { $group: { _id: "$cuisine", count: { $sum: 1 }, restaurants: { $push: "$grades" } } },
  { $match: { count: { $gte: 5 } } },
  { $unwind: "$restaurants" },
  { $unwind: "$restaurants" },
  {
    $group: {
      _id: "$_id",
      avgScore: { $avg: "$restaurants.score" }
    }
  },
  { $project: { _id: 1, avgScore: { $round: ["$avgScore", 2] } } },
  { $sort: { avgScore: -1 } },
  { $limit: 1 }
]);
```

> 💡 *Alternatively, a cleaner approach using a single `$unwind` on the collection directly:*

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  {
    $group: {
      _id: "$cuisine",
      avgScore: { $avg: "$grades.score" },
      restaurantCount: { $addToSet: "$restaurant_id" }
    }
  },
  {
    $project: {
      avgScore: { $round: ["$avgScore", 2] },
      count: { $size: "$restaurantCount" }
    }
  },
  { $match: { count: { $gte: 5 } } },
  { $sort: { avgScore: -1 } },
  { $limit: 1 }
]);
```

**A7.** For each restaurant, compute its **own average score** — show name, borough, and average, sorted descending.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  {
    $group: {
      _id: "$restaurant_id",
      name: { $first: "$name" },
      borough: { $first: "$borough" },
      avgScore: { $avg: "$grades.score" }
    }
  },
  { $project: { name: 1, borough: 1, avgScore: { $round: ["$avgScore", 2] } } },
  { $sort: { avgScore: -1 } }
]);
```

**A8.** Find the **top 5 boroughs** ranked by overall average score.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  {
    $group: {
      _id: "$borough",
      avgScore: { $avg: "$grades.score" }
    }
  },
  { $project: { avgScore: { $round: ["$avgScore", 2] } } },
  { $sort: { avgScore: -1 } },
  { $limit: 5 }
]);
```

---

### 🏅 Rankings & Top-N

**A9.** For each **borough**, find the **restaurant with the single highest score** ever recorded.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  {
    $group: {
      _id: "$restaurant_id",
      name: { $first: "$name" },
      borough: { $first: "$borough" },
      maxScore: { $max: "$grades.score" }
    }
  },
  { $sort: { maxScore: -1 } },
  {
    $group: {
      _id: "$borough",
      topRestaurant: { $first: "$name" },
      highestScore: { $first: "$maxScore" }
    }
  },
  { $sort: { highestScore: -1 } }
]);
```

**A10.** List the **top 10 restaurants by average score** — show name, borough, cuisine, and average score.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  {
    $group: {
      _id: "$restaurant_id",
      name: { $first: "$name" },
      borough: { $first: "$borough" },
      cuisine: { $first: "$cuisine" },
      avgScore: { $avg: "$grades.score" }
    }
  },
  { $project: { name: 1, borough: 1, cuisine: 1, avgScore: { $round: ["$avgScore", 2] } } },
  { $sort: { avgScore: -1 } },
  { $limit: 10 }
]);
```

**A11.** For each borough, find the **most common cuisine**.

```js
db.restaurants.aggregate([
  { $group: { _id: { borough: "$borough", cuisine: "$cuisine" }, count: { $sum: 1 } } },
  { $sort: { "_id.borough": 1, count: -1 } },
  {
    $group: {
      _id: "$_id.borough",
      topCuisine: { $first: "$_id.cuisine" },
      count: { $first: "$count" }
    }
  },
  { $sort: { _id: 1 } }
]);
```

---

### 🔎 Unwinding & Array Operations

**A12.** Count the **total number of 'A', 'B', and 'C' grades** across all restaurants.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  { $match: { "grades.grade": { $in: ["A", "B", "C"] } } },
  {
    $group: {
      _id: "$grades.grade",
      count: { $sum: 1 }
    }
  },
  { $sort: { _id: 1 } }
]);
```

**A13.** Find the **month-wise distribution of inspections** across all years.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  {
    $project: {
      month: { $month: "$grades.date" }
    }
  },
  {
    $group: {
      _id: "$month",
      inspectionCount: { $sum: 1 }
    }
  },
  { $sort: { _id: 1 } }
]);
```

**A14.** Find restaurants that received **at least one 'C' grade** — show name, borough, cuisine, and total 'C' count.

```js
db.restaurants.aggregate([
  { $unwind: "$grades" },
  { $match: { "grades.grade": "C" } },
  {
    $group: {
      _id: "$restaurant_id",
      name: { $first: "$name" },
      borough: { $first: "$borough" },
      cuisine: { $first: "$cuisine" },
      cGradeCount: { $sum: 1 }
    }
  },
  { $sort: { cGradeCount: -1 } }
]);
```

---

### 🧩 Advanced: $facet & Multi-Dimensional

**A15.** Use `$facet` to return — in a **single query** — the count per borough, top 5 cuisines by count, and the overall average score.

```js
db.restaurants.aggregate([
  {
    $facet: {
      // Branch 1: restaurant count per borough
      byBorough: [
        { $group: { _id: "$borough", count: { $sum: 1 } } },
        { $sort: { count: -1 } }
      ],
      // Branch 2: top 5 cuisines by count
      topCuisines: [
        { $group: { _id: "$cuisine", count: { $sum: 1 } } },
        { $sort: { count: -1 } },
        { $limit: 5 }
      ],
      // Branch 3: overall average score (requires unwinding grades)
      overallAvgScore: [
        { $unwind: "$grades" },
        {
          $group: {
            _id: null,
            avgScore: { $avg: "$grades.score" }
          }
        },
        { $project: { _id: 0, avgScore: { $round: ["$avgScore", 2] } } }
      ]
    }
  }
]);
```

> 💡 *`$facet` runs all three branches on the **same input documents** in parallel — you get one result document containing all three arrays.*

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
- Use **`$group`** with accumulators like `$sum`, `$avg`, `$max`, `$min`, `$push`, `$addToSet`.
- Use **`$project`** inside a pipeline to reshape documents or compute new fields.
- Use **`$facet`** to run multiple sub-pipelines in parallel and get results in one go.
- Use **`$round`** inside `$project` to format decimal values.
- Use **`$sort` + `$limit`** together for efficient Top-N queries.
- Use **`$first`** after a `$sort` + `$group` to pick the top item per group.

---

*Happy querying! 🚀*
