PRAGMA foreign_keys = ON;

ALTER TABLE "recipes" ADD COLUMN "preparation_time" INTEGER DEFAULT 1 NOT NULL;

UPDATE "recipes"
SET "preparation_time" = 15
WHERE "id" = 1;

UPDATE "recipes"
SET "preparation_time" = 20
WHERE "id" = 2;

UPDATE "recipes"
SET "preparation_time" = 55
WHERE "id" = 3;

UPDATE "recipes"
SET "preparation_time" = 30
WHERE "id" = 4;

UPDATE "recipes"
SET "preparation_time" = 45
WHERE "id" = 5;

ALTER TABLE "recipe_preparation_steps" ADD COLUMN "preparation_step" INTEGER DEFAULT 1 NOT NULL;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 1
WHERE "id" = 1;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 2
WHERE "id" = 2;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 3
WHERE "id" = 3;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 4
WHERE "id" = 4;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 1
WHERE "id" = 5;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 2
WHERE "id" = 6;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 3
WHERE "id" = 7;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 4
WHERE "id" = 8;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 5
WHERE "id" = 9;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 6
WHERE "id" = 10;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 7
WHERE "id" = 11;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 8
WHERE "id" = 12;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 1
WHERE "id" = 13;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 2
WHERE "id" = 14;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 3
WHERE "id" = 15;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 4
WHERE "id" = 16;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 5
WHERE "id" = 17;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 6
WHERE "id" = 18;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 1
WHERE "id" = 19;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 2
WHERE "id" = 20;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 3
WHERE "id" = 21;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 4
WHERE "id" = 22;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 5
WHERE "id" = 23;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 6
WHERE "id" = 24;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 7
WHERE "id" = 25;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 8
WHERE "id" = 26;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 1
WHERE "id" = 27;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 2
WHERE "id" = 28;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 3
WHERE "id" = 29;

UPDATE "recipe_preparation_steps"
SET "preparation_step" = 4
WHERE "id" = 30;

ALTER TABLE "jt_recipes_ingredients" ADD COLUMN "is_cross_in_shopping_list" INTEGER DEFAULT 0 NOT NULL;

DELETE FROM "meal_plan";

DELETE FROM "jt_meal_plan_recipes";

DROP TABLE "meal_types";

DROP TABLE "jt_meal_plan_meal_types";

CREATE TABLE "meal_types" (
	"id"	INTEGER PRIMARY KEY,
	"name"	TEXT NOT NULL
);

INSERT INTO "meal_types" ("name")
VALUES
("Завтрак"),
("Обед"),
("Ужин"),
("Перекус");

ALTER TABLE "meal_plan" ADD COLUMN "meal_type_id" INTEGER DEFAULT 0 NOT NULL REFERENCES "meal_types"("id") ON UPDATE CASCADE ON DELETE CASCADE;