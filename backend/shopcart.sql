-- ===================== USERS =====================
CREATE TABLE "users"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "name" VARCHAR(255) NOT NULL, -- tên người dùng
    "email" VARCHAR(255) NOT NULL, -- email đăng nhập
    "password" VARCHAR(255) NOT NULL, -- mật khẩu (hash)
    "phone" VARCHAR(20) NULL, -- số điện thoại
    "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NULL DEFAULT CURRENT_TIMESTAMP -- thời gian tạo
);
ALTER TABLE "users" ADD PRIMARY KEY("id");
ALTER TABLE "users" ADD CONSTRAINT "users_email_unique" UNIQUE("email"); -- unique email


-- ===================== ADDRESS =====================
CREATE TABLE "address"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "user_id" UUID NOT NULL, -- FK -> users
    "address_line" TEXT NOT NULL, -- địa chỉ chi tiết
    "city" VARCHAR(100) NULL,
    "district" VARCHAR(100) NULL,
    "ward" VARCHAR(100) NULL,
    "is_default" BOOLEAN NULL -- địa chỉ mặc định
);
ALTER TABLE "address" ADD PRIMARY KEY("id");


-- ===================== PRODUCT =====================
CREATE TABLE "product"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "name" VARCHAR(255) NOT NULL, -- tên sản phẩm
    "price" DECIMAL(12, 2) NOT NULL, -- giá
    "description" TEXT NULL,
    "status" VARCHAR(255) NULL DEFAULT 'ACTIVE', -- trạng thái
    "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE "product" ADD PRIMARY KEY("id");


-- ===================== INVENTORY =====================
CREATE TABLE "inventory"(
    "product_id" UUID NOT NULL, -- FK -> product
    "quantity" INTEGER NOT NULL -- số lượng tồn kho
);
ALTER TABLE "inventory" ADD PRIMARY KEY("product_id");


-- ===================== CART ITEM =====================
CREATE TABLE "cart_item"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "user_id" UUID NOT NULL, -- FK -> users
    "product_id" UUID NOT NULL, -- FK -> product
    "quantity" INTEGER NOT NULL -- số lượng
);
ALTER TABLE "cart_item" ADD CONSTRAINT "cart_item_user_id_product_id_unique" UNIQUE("user_id", "product_id"); -- 1 user chỉ có 1 record / product
ALTER TABLE "cart_item" ADD PRIMARY KEY("id");
CREATE INDEX "cart_item_user_id_index" ON "cart_item"("user_id");
CREATE INDEX "cart_item_product_id_index" ON "cart_item"("product_id");


-- ===================== ORDERS =====================
CREATE TABLE "orders"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "user_id" UUID NOT NULL, -- FK -> users
    "address_id" UUID NOT NULL, -- FK -> address
    "total_price" DECIMAL(12, 2) NOT NULL, -- tổng tiền sản phẩm
    "discount" DECIMAL(12, 2) NULL, -- giảm giá
    "shipping_fee" DECIMAL(12, 2) NULL, -- phí ship
    "final_price" DECIMAL(12, 2) NOT NULL, -- tổng cuối
    "status" VARCHAR(255) NULL DEFAULT 'PENDING', -- trạng thái đơn
    "created_at" TIMESTAMP(0) WITHOUT TIME ZONE NULL DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE "orders" ADD PRIMARY KEY("id");
CREATE INDEX "orders_user_id_index" ON "orders"("user_id");
CREATE INDEX "orders_status_index" ON "orders"("status");


-- ===================== ORDER ITEM =====================
CREATE TABLE "order_item"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "order_id" UUID NOT NULL, -- FK -> orders
    "product_id" UUID NOT NULL, -- FK -> product
    "quantity" INTEGER NOT NULL,
    "price" DECIMAL(12, 2) NOT NULL -- giá tại thời điểm mua
);
ALTER TABLE "order_item" ADD PRIMARY KEY("id");
CREATE INDEX "order_item_order_id_index" ON "order_item"("order_id");
CREATE INDEX "order_item_product_id_index" ON "order_item"("product_id");


-- ===================== COUPON =====================
CREATE TABLE "coupon"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "code" VARCHAR(50) NOT NULL, -- mã giảm giá
    "discount_value" DECIMAL(12, 2) NOT NULL, -- giá trị giảm
    "type" INTEGER NOT NULL, -- loại (%, fixed)
    "expiry_date" TIMESTAMP(0) WITHOUT TIME ZONE NULL, -- hạn sử dụng
    "min_order_value" DECIMAL(12, 2) NULL, -- đơn tối thiểu
    "max_discount" DECIMAL(12, 2) NULL -- giảm tối đa
);
ALTER TABLE "coupon" ADD PRIMARY KEY("id");
ALTER TABLE "coupon" ADD CONSTRAINT "coupon_code_unique" UNIQUE("code");


-- ===================== ORDER COUPON =====================
CREATE TABLE "order_coupon"(
    "order_coupon_id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "order_id" UUID NOT NULL, -- FK -> orders
    "coupon_id" UUID NOT NULL -- FK -> coupon
);
ALTER TABLE "order_coupon" ADD PRIMARY KEY("order_coupon_id");


-- ===================== PAYMENT =====================
CREATE TABLE "payment"(
    "id" UUID NOT NULL DEFAULT UUID_GENERATE_V4(), -- PK
    "order_id" UUID NOT NULL, -- FK -> orders
    "method" INTEGER NOT NULL, -- phương thức thanh toán
    "status" VARCHAR(255) NULL DEFAULT 'PENDING', -- trạng thái
    "paid_at" TIMESTAMP(0) WITHOUT TIME ZONE NULL -- thời gian thanh toán
);
ALTER TABLE "payment" ADD PRIMARY KEY("id");
ALTER TABLE "payment" ADD CONSTRAINT "payment_order_id_unique" UNIQUE("order_id"); -- 1 order có 1 payment


-- ===================== FOREIGN KEYS =====================
ALTER TABLE "order_coupon" ADD CONSTRAINT "order_coupon_order_id_foreign" FOREIGN KEY("order_id") REFERENCES "orders"("id");
ALTER TABLE "order_item" ADD CONSTRAINT "order_item_order_id_foreign" FOREIGN KEY("order_id") REFERENCES "orders"("id");
ALTER TABLE "orders" ADD CONSTRAINT "orders_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("id");
ALTER TABLE "payment" ADD CONSTRAINT "payment_order_id_foreign" FOREIGN KEY("order_id") REFERENCES "orders"("id");
ALTER TABLE "orders" ADD CONSTRAINT "orders_address_id_foreign" FOREIGN KEY("address_id") REFERENCES "address"("id");
ALTER TABLE "order_coupon" ADD CONSTRAINT "order_coupon_coupon_id_foreign" FOREIGN KEY("coupon_id") REFERENCES "coupon"("id");
ALTER TABLE "order_item" ADD CONSTRAINT "order_item_product_id_foreign" FOREIGN KEY("product_id") REFERENCES "product"("id");
ALTER TABLE "inventory" ADD CONSTRAINT "inventory_product_id_foreign" FOREIGN KEY("product_id") REFERENCES "product"("id");
ALTER TABLE "cart_item" ADD CONSTRAINT "cart_item_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("id");
ALTER TABLE "cart_item" ADD CONSTRAINT "cart_item_product_id_foreign" FOREIGN KEY("product_id") REFERENCES "product"("id");
ALTER TABLE "address" ADD CONSTRAINT "address_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("id");