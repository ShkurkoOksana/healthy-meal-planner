PRAGMA foreign_keys = ON;

CREATE TABLE "recipe_categories" (
	"id"	INTEGER PRIMARY KEY,
	"photo" TEXT NOT NULL,
	"name"	TEXT NOT NULL
);

CREATE TABLE "recipe_cousine_types" (
	"id"	INTEGER PRIMARY KEY,
	"name"	TEXT NOT NULL
);

CREATE TABLE "recipe_types" (
	"id"	INTEGER PRIMARY KEY,
	"name"	TEXT NOT NULL
);

CREATE TABLE "recipe_ingredients" (
	"id"	INTEGER PRIMARY KEY,
	"name"	INTEGER NOT NULL
);

CREATE TABLE "ingredient_measures" (
	"id"	INTEGER PRIMARY KEY,
	"name"	INTEGER NOT NULL
);

CREATE TABLE "recipe_preparation_steps" (
	"id"	INTEGER PRIMARY KEY,
	"photo"	TEXT NOT NULL,
	"description"	TEXT NOT NULL
);

CREATE TABLE "recipes" (
	"id"	INTEGER PRIMARY KEY,
	"category_id"	INTEGER NOT NULL,
	"name" TEXT NOT NULL UNIQUE,
	"photo" TEXT NOT NULL,
	"cousine_type_id" INTEGER NOT NULL,
	"energetic_value"  INTEGER NOT NULL,
	"proteins" INTEGER NOT NULL,
    "fats" INTEGER NOT NULL,
    "carbohydrates" INTEGER NOT NULL,
	"is_favorite" INTEGER NOT NULL,
	FOREIGN KEY("category_id") REFERENCES "recipe_categories"("id")
	ON UPDATE CASCADE ON DELETE CASCADE
	FOREIGN KEY("cousine_type_id") REFERENCES "recipe_cousine_types"("id")
	ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "meal_types" (
	"id"	INTEGER PRIMARY KEY,
	"name"	TEXT NOT NULL
);

CREATE TABLE "meal_plan" (
	"id"	INTEGER PRIMARY KEY,
	"date"	TEXT NOT NULL
);

CREATE TABLE "jt_recipes_recipe_types" (
"recipe_id" INTEGER NOT NULL,
"recipe_type_id"  INTEGER NOT NULL,
FOREIGN KEY("recipe_id") REFERENCES "recipes"("id")
	ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY("recipe_type_id") REFERENCES "recipe_types"("id")
	ON UPDATE CASCADE ON DELETE CASCADE
UNIQUE("recipe_id", "recipe_type_id")
);

CREATE TABLE "jt_recipes_ingredients" (
"recipe_id" INTEGER NOT NULL,
"ingredient_id"  INTEGER NOT NULL,
"amount" TEXT NOT NULL,
"is_in_shopping_list" TEXT NOT NULL,
FOREIGN KEY("recipe_id") REFERENCES "recipes"("id")
	ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY("ingredient_id") REFERENCES "recipe_ingredients"("id")
	ON UPDATE CASCADE ON DELETE CASCADE
UNIQUE("recipe_id", "ingredient_id")
);

CREATE TABLE "jt_recipes_preparation_steps" (
"recipe_id" INTEGER NOT NULL,
"preparation_step_id"  INTEGER NOT NULL,
FOREIGN KEY("recipe_id") REFERENCES "recipes"("id")
	ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY("preparation_step_id") REFERENCES "recipe_preparation_steps"("id")
	ON UPDATE CASCADE ON DELETE CASCADE
UNIQUE("recipe_id", "preparation_step_id")
);

CREATE TABLE "jt_meal_plan_recipes" (
	"meal_plan_id"	INTEGER NOT NULL,
	"recipe_id"	INTEGER NOT NULL,
	UNIQUE("meal_plan_id","recipe_id"),
	FOREIGN KEY("meal_plan_id") REFERENCES "meal_plan"("id") ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY("recipe_id") REFERENCES "recipes"("id") ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "jt_meal_plan_meal_types" (
	"meal_plan_id"	INTEGER NOT NULL,
	"meal_type_id"	INTEGER NOT NULL,
	FOREIGN KEY("meal_plan_id") REFERENCES "meal_plan"("id") ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY("meal_type_id") REFERENCES "meal_types"("id") ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE("meal_plan_id","meal_type_id")
);

CREATE TABLE "jt_recipe_ingredients_ingredient_measures" (
"recipe_ingredient_id" INTEGER NOT NULL,
"ingredient_measure_id"  INTEGER NOT NULL,
FOREIGN KEY("recipe_ingredient_id") REFERENCES "recipe_ingredients"("id")
	ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY("ingredient_measure_id") REFERENCES "ingredient_measures"("id")
	ON UPDATE CASCADE ON DELETE CASCADE
UNIQUE("recipe_ingredient_id", "ingredient_measure_id")
);

INSERT INTO "recipe_types" ("name")
VALUES
("Диетический"),
("Для детей"),
("Блюдо из яиц"),
("Морепродукты"),
("Рыба");

INSERT INTO "recipe_preparation_steps" ("photo", "description")
VALUES
("https://images.unsplash.com/photo-1498654077810-12c21d4d6dc3?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
 "Яйца берите крупные отборные. Чтобы яичница получилась не только вкусной, но и красивой, лучше используйте деревенские яйца с крупным желтком насыщенного желтого цвета. Яйца обязательно берите свежие. Перед началом приготовления их необходимо помыть"),
("https://images.unsplash.com/photo-1589714379796-37d4bc8655c0?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2052&q=80",
 "Поставьте сковороду на медленный огонь (у меня это режим 2 из 9) и хорошо прогрейте. На это может потребоваться до 5-7 минут. Выложите кусочек сливочного масла и растопите. Когда масло начнет пузыриться, аккуратно и не торопясь разбейте в сковороду яйца, стараясь сохранить желток целым. Очень важно прогреть сковороду заранее, чтобы белок при соприкосновении с горячей поверхностью сразу схватился и не растекся по всей сковороде тонким слоем."),
("https://images.unsplash.com/photo-1620800981162-eb559427ad08?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
"Жарьте яичницу на медленном огне без крышки около 5 минут. Белок должен схватиться, а желток остаться полужидким и ярким. Чтобы желток не затянулся белесой пленкой и сохранил полужидкую консистенцию, сковороду лучше не накрывайте крышкой. А если и накрываете, то не дольше 1-2 минут. При этом огонь нельзя добавлять, иначе яичница быстро поджарится и желток затвердеет."),
("https://images.unsplash.com/photo-1620800981162-eb559427ad08?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
"Переложите готовую яичницу на блюдо, украсьте половинками помидоров черри, ломтиками хлеба  и веточками зелени. Приятного аппетита!."),
("https://images.unsplash.com/photo-1615802546478-7380efa2d365?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Подготавливаем необходимые продукты и специи для приготовления греческого салата с насыщенным и неординарным вкусом."),
("https://plus.unsplash.com/premium_photo-1668616815449-b61c3f4d4f44?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
"Наливаем в емкость оливковое масло. Добавляем одну чайную ложку прованских трав и выжимаем сок лимона."),
("https://images.unsplash.com/photo-1622205313162-be1d5712a43f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3004&q=80",
"Крошим кусочками хорошо промытые листья салата и выкладываем их в объемную миску для подачи салата."),
("https://images.unsplash.com/photo-1589621316382-008455b857cd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Нарезаем произвольным средним кубиком огурец. Равномерным слоем выкладываем нарезанный кубиком огурец сверху листьев салата"),
("https://images.unsplash.com/photo-1597226051193-7335b1796546?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Нарезаем произвольным средним кубиком помидоры."),
("https://images.unsplash.com/photo-1661349008073-136bed6e6788?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
"Выкладываем и равномерно размещаем кусочки сыра в салате."),
("https://images.unsplash.com/photo-1596099477998-880bc06e09f9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=988&q=80",
"Выкладываем в салат цельные маслины."),
("https://images.unsplash.com/photo-1505253716362-afaea1d3d1af?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3648&q=80",
"Заправляем салат приготовленным и настоявшимся соусом. Добавляем в салат по вкусу смесь из черного с красным перцев."),
("https://images.unsplash.com/photo-1635321593217-40050ad13c74?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3496&q=80",
"Как сварить борщ со свежей капустой, свеклой и мясом? Подготовьте для этого необходимые ингредиенты. Капусту берите свежую. Овощи хорошо помойте от загрязнений и очистите от кожуры."),
("https://images.unsplash.com/photo-1604503468506-a8da13d82791?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
"Свинину можно использовать любую часть. Но если варить свинину на кости, то бульон будет получаться более наваристый. Поместите свинину в холодную воду, доведите до кипения и варите 1-1,5 часа. При закипании снимайте пенку, которая будет появляться. Готовый бульон процедите. Мясо нарежьте на кусочки и верните в кастрюлю."),
("https://images.unsplash.com/photo-1566320239257-2a4d2745d771?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Капусту нашинкуйте, а картофель нарежьте на кусочки. Выложите капусту и картофель в кастрюлю с кипящим бульоном и оставьте вариться минут на 15."),
("https://images.unsplash.com/photo-1567137827022-fbe18eff7275?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2274&q=80",
"В это время мелко нарежьте лук и натрите на терке морковь. Обжарьте лук и морковь в течение 4-5 минут на небольшом огне. Затем добавьте в зажарку нарезанные помидоры. Обжаривайте все вместе еще 3-4 минуты. За это время помидоры пустят сок и станут мягкие. Готовую зажарку выложите в кастрюлю."),
("https://images.unsplash.com/photo-1503623758111-863eb2abd7a9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
"Отдельно обжарьте свеклу, натертую на терке. Добавьте к ней немного томатной пасты. Она придаст борщу небольшую кислинку и насыщенный цвет. На жарку свеклы уйдет еще минут 5-7. За время жарки она немного уменьшится в размере."),
("https://images.unsplash.com/photo-1526401363794-c96708fb8089?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80",
"Выложите обжаренную свеклу в кастрюлю. Добавьте лавровый лист. По вкусу добавьте немного соли и специи. Перемешайте, доведите до кипения и снимите с огня. Кипятить со свеклой борщ не надо. От этого он потеряет цвет. Готовый борщ накройте крышкой и оставьте постоять минут на 15-20, чтобы он немного настоялся. Готовый борщ подавайте украсив зеленью и добавьте ложечку сметаны. Приятного аппетита!"),
("https://images.unsplash.com/photo-1556386734-4227a180d19e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1305&q=80",
"Как сделать салат цезарь классический с креветками? Первым делом подготовьте все  необходимые ингредиенты по списку. Креветки для салата лучше взять тигровые или королевские, но и с обычными получится не менее вкусно. Какие листы салата лучше использовать? В классическом рецепте используют Романо, можно взять Латук, Эндвий, Айсберг или Пекинскую капусту. Помидоры, лимон и листья ополосните в чистой воде, обсушите салфетками."),
("https://images.unsplash.com/photo-1580317092099-ade9937dee4f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1321&q=80",
"Креветки выньте из морозильной камеры и разморозьте при комнатной температуре. Для более быстрой разморозки можно залить креветки прохладной водой. Удалите с моллюсков панцири и головы, хвостики можно оставить, либо тоже убрать- на ваше усмотрение. Далее существует два способа термической обработки креветок. Их можно отварить в кипящей воде до готовности или замариновать и обжарить в сковороде. Я чаще готовлю вторым способом, так вкус салата, как мне кажется, получается более насыщенным. Итак, в глубокую небольшую чашку отправьте креветки, добавьте 1ст.л. сока лимона, 2 ст.л. растительного или оливкового масла и щепотку соли. Если у вас есть вустерский соус, то добавьте его тоже. Оставьте на 10 мин."),
("https://images.unsplash.com/photo-1587157987149-57586c595a6b?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Обжарьте креветки на горячей сковороде пару минут с одной стороны. И ещё столько же с другой."),
("https://images.unsplash.com/photo-1508616185939-efe767994166?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1335&q=80",
"С белого хлеба (можно использовать батон) срежьте корочки. Нарежьте хлеб небольшими кубиками размером 1-2 см. Обжарьте на небольшом количестве оливкового или растительного масла. При желании можно добавить немного чеснока, так сухарики получатся ароматными."),
("https://images.unsplash.com/photo-1586276424667-06c0b45b90ba?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Яйца залейте холодной водой и поставьте на плиту. После закипания пламя слегка убавьте и варите яйца одну минуту. Они должны получиться всмятку. Нам понадобятся только желтки для приготовления заправки."),
("https://images.unsplash.com/photo-1611516081814-55d97d5a7488?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2376&q=80",
"Подготовьте соус. Для этого в чашу блендера поместите два желтка, горчицу, 2 ст.л. сока лимона, бальзамический уксус, 4 ст.л. оливкового или растительного масла, чеснок, соль, сахар и чёрный молотый перец. Взбейте всё до однородной массы."),
("https://images.unsplash.com/photo-1622205313162-be1d5712a43f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3004&q=80",
"Осталось собрать салат. Листы нарвите руками произвольно, отправьте на плоскую тарелку первым слоем."),
("https://images.unsplash.com/photo-1551248429-40975aa4de74?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1890&q=80",
"Далее идёт очередь сухариков и томатов черри. Помидоры можно разрезать пополам или четвертинками. Выложите следующим слоем креветки. Равномерно полейте салат соусом. И добавьте сыр, натёртый на мелкой тёрке. По желанию сверху посыпьте сухариками и украсьте зеленью. Цезарь готов, подавайте сразу же! Слегка перемешайте салат у себя в тарелке и насладитесь потрясающим вкусом!"),
("https://images.unsplash.com/photo-1510130387422-82bed34b37e9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1335&q=80",
"Как потушить минтай с луком и морковью? Подготовьте продукты по списку. Рыбу заранее разморозьте, можно не полностью, так ее будет легче разделывать. Морковь хорошо помойте щеткой под проточной водой, затем обсушите. Репчатый лук очистите от шелухи, помойте, обсушите. Рыбу очистите от чешуи, отрежьте голову, хвост, плавники. Хорошо промойте под струей холодной воды, обсушите бумажными полотенцами. Затем порежьте рыбу на порционные кусочки."),
("https://plus.unsplash.com/premium_photo-1663047563264-20f61cc983a8?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Минтай посолите и поперчите. На сковороде разогрейте растительное масло и быстро обжарьте минтая с двух сторон, не отходя от плиты. Будьте внимательны: если в горячее масло попадет капля воды, оно начнет шипеть и брызгаться. Поэтому важно, чтобы на рыбе воды не оставалось."),
("https://images.unsplash.com/photo-1636065641381-a3bf345e9a02?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
"Натрите на крупной терке морковь и добавьте к рыбе. Влейте в сковороду полстакана воды, тушите блюдо под крышкой минут 10. За это время и рыба, и овощи успеют приготовиться. Перед выключением плиты проверьте их на готовность на всякий случай, потому чтоваша плита может готовить быстрее или медленнее моей."),
("https://images.unsplash.com/photo-1485921325833-c519f76c4927?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1364&q=80",
"Подавайте тушеный минтай с морковью и луком прямо на сковороде, присыпав свежей зеленью. Или можете разложить по тарелкам с гарниром или без гарнира, со свежим хлебом, который очень вкусно макать в образовавшийся при тушении рыбы с овощами соус.");

INSERT INTO "recipe_cousine_types" ("name")
VALUES
("Русская кухня"),
("Европейкая кухня"),
("Украинская кухня"),
("Американская кухня");

INSERT INTO "recipe_categories" ("photo", "name")
VALUES
("https://images.unsplash.com/photo-1611068120813-eca5a8cbf793?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80", "Первые блюда"),
("https://images.unsplash.com/photo-1432139509613-5c4255815697?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=985&q=80", "Вторые блюда"),
("https://images.unsplash.com/photo-1537785713284-0015ce8a145c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80", "Салаты"),
("https://images.unsplash.com/photo-1592415486689-125cbbfcbee2?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2050&q=80",  "Закуски"),
("https://images.unsplash.com/photo-1509440159596-0249088772ff?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2372&q=80", "Выпечка"),
("https://images.unsplash.com/photo-1644882767097-2f1748d80ec9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80", "Соусы и маринады"),
("https://images.unsplash.com/photo-1582438936711-60b28a7195cd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2369&q=80", "Заготовки"),
("https://images.unsplash.com/photo-1497534446932-c925b458314e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1372&q=80", "Напитки"),
("https://images.unsplash.com/photo-1508737804141-4c3b688e2546?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=986&q=80", "Десерты"),
("https://images.unsplash.com/photo-1596560548464-f010549b84d7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80", "Гарниры");

INSERT INTO "recipe_categories" ("photo", "name")
VALUES
("https://images.unsplash.com/photo-1611068120813-eca5a8cbf793?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80", "Первые блюда"),
("https://images.unsplash.com/photo-1432139509613-5c4255815697?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=985&q=80", "Вторые блюда"),
("https://images.unsplash.com/photo-1537785713284-0015ce8a145c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80", "Салаты"),
("https://images.unsplash.com/photo-1592415486689-125cbbfcbee2?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2050&q=80",  "Закуски"),
("https://images.unsplash.com/photo-1509440159596-0249088772ff?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2372&q=80", "Выпечка"),
("https://images.unsplash.com/photo-1644882767097-2f1748d80ec9?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=987&q=80", "Соусы и маринады"),
("https://images.unsplash.com/photo-1582438936711-60b28a7195cd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2369&q=80", "Заготовки"),
("https://images.unsplash.com/photo-1497534446932-c925b458314e?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1372&q=80", "Напитки"),
("https://images.unsplash.com/photo-1508737804141-4c3b688e2546?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=986&q=80", "Десерты"),
("https://images.unsplash.com/photo-1596560548464-f010549b84d7?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80", "Гарниры");

INSERT INTO "recipe_ingredients" ("name")
VALUES
("Яйцо"),
("Топленное масло"),
("Сыр Фета"),
("Болгарский перец"),
("Помидор"),
("Огурец"),
("Салат-латук"),
("Репчатый лук"),
("Маслины"),
("Лимон"),
("Оливковое масло"),
("Смесь трав"),
("Соль"),
("Черный перец молотый"),
("Креветки"),
("Листья салата"),
("Помидоры черри"),
("Чеснок"),
("Белый хлеб"),
("Бальзамический уксус"),
("Горчица"),
("Вода"),
("Говядина"),
("Свекла"),
("Морковь"),
("Томатная паста"),
("Белокачанная капуста"),
("Картофель"),
("Лавровый лист"),
("Петрушка"),
("Укроп"),
("Минтай"),
("Мука зеленой гречки");

INSERT INTO "ingredient_measures" ("name")
VALUES
("шт."),
("по вкусу"),
("гр."),
("ст.ложки"),
("л."),
("зуб."),
("ч.ложка");

INSERT INTO "recipes" ("category_id", "name", "photo", "cousine_type_id", "energetic_value", "proteins", "fats", "carbohydrates", "is_favorite")
VALUES
(2, "Глазунья","https://images.unsplash.com/photo-1612487439139-c2d7bac13577?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80", 1, 199, 3, 13, 10, 1),
(3, "Греческий салат", "https://images.unsplash.com/photo-1625944230945-1b7dd3b949ab?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1760&q=80", 2, 117, 3, 10, 4, 0),
(1, "Борщ", "https://images.unsplash.com/photo-1527976746453-f363eac4d889?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=3432&q=80", 3, 51, 3, 3, 4, 1),
(3, "Салат цезарь с креветками", "https://images.unsplash.com/photo-1551248429-40975aa4de74?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1980&q=80", 4, 183, 9, 12, 8, 1),
(2, "Минтай тушенный", "https://images.unsplash.com/photo-1485921325833-c519f76c4927?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1364&q=80", 1, 117, 12, 7, 2, 0);

INSERT INTO "meal_types" ("name")
VALUES
("Завтрак"),
("Обед"),
("Ужин"),
("Перекус");

INSERT INTO "meal_plan" ("date")
VALUES
("18.01.2024");

INSERT INTO "jt_meal_plan_meal_types" ("meal_plan_id", "meal_type_id")
VALUES
(1, 1);

INSERT INTO "jt_meal_plan_recipes" ("meal_plan_id", "recipe_id")
VALUES
(1, 1);

INSERT INTO "jt_recipes_ingredients" ("recipe_id", "ingredient_id", "amount", "is_in_shopping_list")
VALUES
(1, 1, "1.0",  1),
(1, 13, "", 0),
(1, 2, "8.3", 0),
(2, 7, "20.0",  0),
(2, 6, "0.2", 0),
(2, 4, "0.1", 0),
(2, 5, "0.6",  0),
(2, 9, "3.0", 0),
(2, 3, "14.0", 1),
(2, 11, "0.6",  1),
(2, 12, "", 0),
(2, 10, "0.1", 0),
(2, 13, "",  0),
(2, 14, "", 0),
(3, 23, "100.0", 0),
(3, 27, "33.0",  0),
(3, 28, "0.5", 0),
(3, 8, "0.2", 0),
(3, 25, "0.2",  0),
(3, 5, "0.3", 0),
(3, 24, "0.2", 0),
(3, 26, "0.3",  0),
(3, 22, "0.4", 0),
(3, 29, "0.2", 0),
(3, 13, "",  0),
(3, 14, "", 0),
(4, 15, "200.0", 0),
(4, 16, "150.0",  0),
(4, 17, "0.5", 0),
(4, 1, "1.0", 0),
(4, 18, "1.5",  0),
(4, 10, "0.5", 0),
(4, 19, "100.0", 0),
(4, 11, "3.0",  0),
(4, 20, "0.5", 0),
(4, 21, "0.5", 0),
(4, 13, "",  0),
(4, 14, "", 0),
(5, 32, "300.0", 0),
(5, 25, "0.15",  0),
(5, 8, "0.5", 0),
(5, 2, "25.0", 0),
(5, 13, "",  0),
(5, 14, "", 0);

INSERT INTO "jt_recipes_preparation_steps" ("recipe_id", "preparation_step_id")
VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(2, 5),
(2, 6),
(2, 7),
(2, 8),
(2, 9),
(2, 10),
(2, 11),
(2, 12),
(3, 13),
(3, 14),
(3, 15),
(3, 16),
(3, 17),
(3, 18),
(4, 19),
(4, 20),
(4, 21),
(4, 22),
(4, 23),
(4, 24),
(4, 25),
(4, 26),
(5, 27),
(5, 28),
(5, 29),
(5, 30);

INSERT INTO "jt_recipes_recipe_types" ("recipe_id", "recipe_type_id")
VALUES
(1, 1),
(1, 2),
(1, 3),
(2, 1),
(3, 2),
(4, 4),
(5, 5),
(5, 1);

INSERT INTO "jt_recipe_ingredients_ingredient_measures" ("recipe_ingredient_id", "ingredient_measure_id")
VALUES
(1, 1),
(2, 3),
(3, 3),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1),
(11, 4),
(12, 2),
(13, 2),
(14, 2),
(15, 3),
(16, 3),
(17, 1),
(18, 6),
(19, 3),
(20, 4),
(21, 7),
(22, 5),
(23, 3),
(24, 1),
(25, 1),
(26, 4),
(27, 3),
(28, 1),
(29, 1),
(30, 1),
(31, 1),
(32, 3),
(33, 3);




