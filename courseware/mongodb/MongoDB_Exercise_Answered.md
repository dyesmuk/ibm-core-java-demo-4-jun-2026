# 🍽️ MongoDB Restaurant Queries — Practice Exercise (Answered)

> **Scenario:** You've been handed a dataset of NYC restaurants. Your job: slice, dice, and query your way through it like a seasoned data chef. 🧑‍🍳

---

## ⚙️ Setup

1. Download the dataset → `restaurants.json`
2. Import it into your local MongoDB server:
   ```bash
   mongoimport --db nyc --collection restaurants --file restaurants.json --jsonArray
   ```
3. Open your MongoDB shell and switch to the database:
   ```js
   use nyc
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

### 🏆 Score-Based Filtering

**Q8.** Find restaurants that achieved a score **greater than 90**.

```js
db.restaurants.find({ grades: { $elemMatch: { score: { $gt: 90 } } } });
```

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

### 🌍 Geo + Cuisine Filtering

**Q11.** Find restaurants that:
- Do not serve American cuisine
- Have a score greater than 70
- Are located at a latitude less than -65.754168

```js
db.restaurants.find({
  $and: [
    { cuisine: { $ne: "American " } },
    { "grades.score": { $gt: 70 } },
    { "address.coord": { $lt: -65.754168 } }
  ]
});
```

**Q12.** Same as Q11, but without using the `$and` operator.

```js
db.restaurants.find({
  cuisine: { $ne: "American " },
  "grades.score": { $gt: 70 },
  "address.coord": { $lt: -65.754168 }
});
```

**Q17.** Find restaurants in the Bronx that serve either American or Chinese cuisine.

```js
db.restaurants.find({
  borough: "Bronx",
  $or: [{ cuisine: "American " }, { cuisine: "Chinese" }]
});
```

### 🗺️ Borough Filtering

**Q18.** Display restaurants in Staten Island, Queens, Bronx, or Brooklyn.

```js
db.restaurants.find(
  { borough: { $in: ["Staten Island", "Queens", "Bronx", "Brooklyn"] } },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q19.** Display restaurants not in those four boroughs.

```js
db.restaurants.find(
  { borough: { $nin: ["Staten Island", "Queens", "Bronx", "Brooklyn"] } },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

### 🔤 Name Pattern Matching (Regex)

**Q14.** Restaurants whose name starts with `Wil`.

```js
db.restaurants.find(
  { name: /^Wil/ },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q15.** Restaurants whose name ends with `ces`.

```js
db.restaurants.find(
  { name: /ces$/ },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q16.** Restaurants whose name contains `Reg`.

```js
db.restaurants.find(
  { name: /Reg/ },
  { restaurant_id: 1, name: 1, borough: 1, cuisine: 1, _id: 0 }
);
```

**Q31.** Restaurants with `mon` anywhere in the name.

```js
db.restaurants.find(
  { name: /mon/i },
  { name: 1, borough: 1, cuisine: 1, "address.coord": 1, _id: 0 }
);
```

**Q32.** Restaurants whose name begins with `Mad`.

```js
db.restaurants.find(
  { name: /^Mad/ },
  { name: 1, borough: 1, cuisine: 1, "address.coord": 1, _id: 0 }
);
```

### 🎯 Combined & Complex Conditions

**Q13.** Non-American cuisine, grade A, not in Brooklyn, sorted by cuisine descending.

```js
db.restaurants.find({
  cuisine: { $ne: "American " },
  "grades.grade": "A",
  borough: { $ne: "Brooklyn" }
}).sort({ cuisine: -1 });
```

**Q21.** Serve neither American nor Chinese cuisine OR name starts with Wil.

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

### 📅 Array & Date Queries

**Q22.** Grade A, score 11, on 2014-08-11.

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

**Q23.** Second element of grades array has grade A, score 9, date 2014-08-11.

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

**Q24.** Second coordinate element is more than 42 and up to 52.

```js
db.restaurants.find(
  { "address.coord.1": { $gt: 42, $lte: 52 } },
  { restaurant_id: 1, name: 1, address: 1, _id: 0 }
);
```

### 📊 Sorting

**Q25.** Sort by name ascending.

```js
db.restaurants.find().sort({ name: 1 });
```

**Q26.** Sort by name descending.

```js
db.restaurants.find().sort({ name: -1 });
```

**Q27.** Sort by cuisine ascending and borough descending.

```js
db.restaurants.find().sort({ cuisine: 1, borough: -1 });
```

### 🛠️ Miscellaneous

**Q28.** Check whether all address documents contain a street field.

```js
db.restaurants.find({ "address.street": { $exists: false } });
```

**Q29.** Select all documents where coord values are of type Double.

```js
db.restaurants.find({ "address.coord": { $type: 1 } });
```
