-- =========================================================
-- SCHEMA
-- =========================================================

CREATE SCHEMA "public";



-- =========================================================
-- USERS
-- =========================================================
-- Lưu thông tin tài khoản người dùng
-- Một user có thể:
-- - có nhiều địa chỉ
-- - có nhiều đơn hàng
-- - có nhiều sản phẩm trong giỏ hàng
-- =========================================================

CREATE TABLE "users" (

	-- ID người dùng
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Email đăng nhập, unique toàn hệ thống
	"email" varchar(255) NOT NULL
	CONSTRAINT "users_email_unique" UNIQUE,

	-- Mật khẩu đã hash (bcrypt/argon2)
	"hash_password" varchar(255) NOT NULL,

	-- Số điện thoại
	"phone" varchar(20),

	-- Thời gian tạo tài khoản
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP,

	-- Họ tên hiển thị
	"full_name" varchar(255)
);



-- =========================================================
-- ADDRESSES
-- =========================================================
-- Danh sách địa chỉ giao hàng của user
-- Một user có thể lưu nhiều địa chỉ:
-- - nhà riêng
-- - công ty
-- - người thân
--
-- Khi đặt hàng:
-- address sẽ được copy sang bảng orders
-- để lưu snapshot tại thời điểm đặt hàng
-- =========================================================

CREATE TABLE "addresses" (

	-- ID địa chỉ
	"id" uuid DEFAULT uuid_generate_v4(),

	-- User sở hữu địa chỉ
	"user_id" uuid NOT NULL,

	-- Địa chỉ chi tiết
	"address_line" text NOT NULL,

	-- Thành phố
	"city" varchar(255) NOT NULL,

	-- Quận/huyện
	"district" varchar(255) NOT NULL,

	-- Phường/xã
	"ward" varchar(255) NOT NULL,

	-- Địa chỉ mặc định của user
	"is_default" boolean DEFAULT false NOT NULL,

	CONSTRAINT "address_pkey" PRIMARY KEY("id")
);



-- =========================================================
-- CATEGORY
-- =========================================================
-- Danh mục sản phẩm
-- Ví dụ:
-- - Laptop
-- - Điện thoại
-- - Quần áo
-- =========================================================

CREATE TABLE "category" (

	-- ID danh mục
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Tên danh mục
	"name" varchar(255) NOT NULL
	CONSTRAINT "category_name_key" UNIQUE,

	-- Thời gian tạo
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);



-- =========================================================
-- PRODUCT
-- =========================================================
-- Thông tin sản phẩm
--
-- Lưu ý:
-- price là giá hiện tại
-- Khi user mua hàng:
-- giá sẽ được copy sang order_items.price
-- =========================================================

CREATE TABLE "product" (

	-- ID sản phẩm
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Tên sản phẩm
	"name" varchar(255) NOT NULL,

	-- Giá hiện tại
	"price" numeric(12, 2) NOT NULL,

	-- Mô tả sản phẩm
	"description" text,

	-- Trạng thái sản phẩm
	-- ACTIVE / INACTIVE
	"status" varchar(255) DEFAULT 'ACTIVE',

	-- Thời gian tạo
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP,

	-- Danh mục sản phẩm
	"category_id" uuid,

	-- Slug SEO
	"slug" varchar(255)
	CONSTRAINT "product_slug_key" UNIQUE,

	-- Sản phẩm nổi bật
	"is_featured" boolean DEFAULT false
);



-- =========================================================
-- PRODUCT_IMAGE
-- =========================================================
-- Danh sách ảnh của sản phẩm
-- Một sản phẩm có thể có nhiều ảnh
-- =========================================================

CREATE TABLE "product_image" (

	-- ID ảnh
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Product sở hữu ảnh
	"product_id" uuid NOT NULL,

	-- URL ảnh
	"image_url" text NOT NULL,

	-- Thứ tự hiển thị
	"sort_order" integer DEFAULT 0,

	-- Thời gian tạo
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP,

	-- Ảnh thumbnail chính
	"is_thumbnail" boolean DEFAULT false
);



-- =========================================================
-- INVENTORY
-- =========================================================
-- Tồn kho sản phẩm
-- Mỗi product có 1 inventory
-- =========================================================

CREATE TABLE "inventory" (

	-- Product được quản lý tồn kho
	"product_id" uuid PRIMARY KEY,

	-- Số lượng tồn kho còn lại
	"quantity" integer NOT NULL
);



-- =========================================================
-- CART_ITEM
-- =========================================================
-- Giỏ hàng của user
-- Mỗi dòng = 1 sản phẩm trong giỏ
--
-- Nếu user thêm cùng sản phẩm:
-- -> update quantity
-- không tạo dòng mới
-- =========================================================

CREATE TABLE "cart_item" (

	-- User sở hữu giỏ hàng
	"user_id" uuid NOT NULL,

	-- Sản phẩm trong giỏ
	"product_id" uuid NOT NULL,

	-- Số lượng sản phẩm
	"quantity" integer DEFAULT 1 NOT NULL,

	-- Thời gian thêm vào giỏ
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP,

	-- ID cart item
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Không cho cùng product xuất hiện nhiều lần
	-- trong giỏ hàng của cùng user
	CONSTRAINT "cart_item_user_product_unique"
	UNIQUE("user_id","product_id"),

	-- Quantity phải > 0
	CONSTRAINT "cart_item_quantity_check"
	CHECK ((quantity > 0))
);



-- =========================================================
-- COUPONS
-- =========================================================
-- Mã giảm giá
--
-- coupon_scope:
-- - ORDER: giảm đơn hàng
-- - SHIPPING: giảm phí ship
--
-- discount_type:
-- - FIXED
-- - PERCENT
-- =========================================================

CREATE TABLE "coupons" (

	-- ID coupon
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Mã coupon
	"code" varchar(50) NOT NULL
	CONSTRAINT "coupons_code_unique" UNIQUE,

	-- Giá trị giảm
	"discount_value" numeric(19, 2) NOT NULL,

	-- Thời gian hết hạn
	"expiry_date" timestamp,

	-- Giá trị đơn tối thiểu
	"min_order_value" numeric(19, 2)
	DEFAULT '0' NOT NULL,

	-- Giảm tối đa
	"max_discount" numeric(19, 2),

	-- FIXED / PERCENT
	"discount_type" varchar(20) NOT NULL,

	-- Thời gian bắt đầu áp dụng
	"start_date" timestamp,

	-- Mỗi user được dùng bao nhiêu lần
	"usage_per_user" integer DEFAULT 1 NOT NULL,

	-- ACTIVE / INACTIVE
	"status" varchar(20) DEFAULT 'ACTIVE' NOT NULL,

	-- Thời gian tạo
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP,

	-- ORDER / SHIPPING
	"coupon_scope" varchar(255) NOT NULL,

	CONSTRAINT "coupons_discount_type_check"
	CHECK (
		(discount_type)::text = ANY (
			(ARRAY[
				'FIXED'::character varying,
				'PERCENT'::character varying
			])::text[]
		)
	),

	CONSTRAINT "coupons_scope_check"
	CHECK (
		(coupon_scope)::text = ANY (
			(ARRAY[
				'ORDER'::character varying,
				'SHIPPING'::character varying
			])::text[]
		)
	),

	CONSTRAINT "coupons_status_check"
	CHECK (
		(status)::text = ANY (
			(ARRAY[
				'ACTIVE'::character varying,
				'INACTIVE'::character varying
			])::text[]
		)
	)
);



-- =========================================================
-- SHIPPING METHODS
-- =========================================================
-- Danh sách phương thức vận chuyển
--
-- Ví dụ:
-- - Giao tiết kiệm
-- - Giao nhanh
-- - Hỏa tốc
-- =========================================================

CREATE TABLE "shipping_methods" (

	-- ID phương thức ship
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Code unique
	"code" varchar(255) NOT NULL
	CONSTRAINT "shipping_methods_code_key" UNIQUE,

	-- Tên hiển thị
	"name" varchar(255) NOT NULL,

	-- Mô tả
	"description" text,

	-- Phí ship cơ bản
	"base_fee" numeric(19, 3)
	DEFAULT '0' NOT NULL,

	-- Thời gian giao dự kiến min
	"estimated_days_min" integer,

	-- Thời gian giao dự kiến max
	"estimated_days_max" integer,

	-- Có đang hoạt động không
	"is_active" boolean DEFAULT true,

	-- Thời gian tạo
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);



-- =========================================================
-- PAYMENT METHODS
-- =========================================================
-- Danh sách phương thức thanh toán
--
-- Ví dụ:
-- - COD
-- - MOMO
-- - VNPAY
-- =========================================================

CREATE TABLE "payment_methods" (

	-- ID payment method
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Code unique
	"code" varchar(50) NOT NULL
	CONSTRAINT "payment_methods_code_key" UNIQUE,

	-- Tên hiển thị
	"name" varchar(255) NOT NULL,

	-- Có đang hoạt động không
	"is_active" boolean DEFAULT true,

	-- Thời gian tạo
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP
);



-- =========================================================
-- ORDERS
-- =========================================================
-- Bảng đơn hàng chính
--
-- Mỗi dòng = 1 đơn hàng
--
-- Shipping info được copy từ addresses
-- để giữ snapshot tại thời điểm đặt hàng
--
-- subtotal:
-- tổng tiền hàng
--
-- final_price:
-- tổng cuối cùng user phải trả
-- =========================================================

CREATE TABLE "orders" (

	-- ID đơn hàng
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- User đặt hàng
	"user_id" uuid NOT NULL,

	-- Tên người nhận
	"shipping_full_name" varchar(255) NOT NULL,

	-- SĐT người nhận
	"shipping_phone" varchar(20) NOT NULL,

	-- Địa chỉ giao hàng
	"shipping_address_line" text NOT NULL,

	-- Thành phố
	"shipping_city" varchar(255) NOT NULL,

	-- Quận/huyện
	"shipping_district" varchar(255) NOT NULL,

	-- Phường/xã
	"shipping_ward" varchar(255) NOT NULL,

	-- Phương thức vận chuyển
	"shipping_method_id" uuid,

	-- Tên phương thức ship snapshot
	"shipping_method_name" varchar(255),

	-- Phí ship
	"shipping_fee" numeric(19, 2) DEFAULT '0',

	-- Số ngày giao dự kiến tối thiểu
	"estimated_delivery_min" integer,

	-- Số ngày giao dự kiến tối đa
	"estimated_delivery_max" integer,

	-- Tổng tiền hàng
	"subtotal" numeric(19, 2) NOT NULL,

	-- Tổng discount
	"discount" numeric(19, 2) DEFAULT '0',

	-- Tổng cuối cùng user phải trả
	"final_price" numeric(19, 2) NOT NULL,

	-- Coupon snapshot
	"coupon_code" varchar(50),

	-- Số tiền coupon đã giảm
	"coupon_discount_amount" numeric(19, 2) DEFAULT '0',

	-- Trạng thái đơn hàng
	-- PENDING
	-- CONFIRMED
	-- PROCESSING
	-- SHIPPING
	-- DELIVERED
	-- CANCELLED
	-- FAILED
	"status" varchar(50) DEFAULT 'PENDING',

	-- Thời gian hết hạn thanh toán
	"expired_at" timestamp,

	-- Thời gian tạo đơn
	"created_at" timestamp DEFAULT CURRENT_TIMESTAMP,

	-- Thời gian update gần nhất
	"updated_at" timestamp DEFAULT CURRENT_TIMESTAMP,

	-- Address gốc user đã chọn
	"address_id" uuid NOT NULL,

	CONSTRAINT "orders_status_check"
	CHECK (
		(status)::text = ANY (
			(ARRAY[
				'PENDING'::character varying,
				'CONFIRMED'::character varying,
				'PROCESSING'::character varying,
				'SHIPPING'::character varying,
				'DELIVERED'::character varying,
				'CANCELLED'::character varying,
				'FAILED'::character varying
			])::text[]
		)
	)
);



-- =========================================================
-- ORDER ITEMS
-- =========================================================
-- Danh sách sản phẩm trong đơn hàng
--
-- Giá sản phẩm được snapshot tại thời điểm mua
-- để tránh product.price thay đổi sau này
-- =========================================================

CREATE TABLE "order_items" (

	-- ID order item
	"id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Đơn hàng sở hữu item
	"order_id" uuid NOT NULL,

	-- Product được mua
	"product_id" uuid NOT NULL,

	-- Số lượng
	"quantity" integer NOT NULL,

	-- Giá tại thời điểm mua
	"price" numeric(19, 2) NOT NULL
);



-- =========================================================
-- ORDER COUPONS
-- =========================================================
-- Coupon đã áp dụng cho đơn hàng
--
-- Một order có thể áp dụng:
-- - coupon giảm giá đơn
-- - coupon freeship
-- =========================================================

CREATE TABLE "order_coupons" (

	-- ID mapping
	"order_coupon_id" uuid PRIMARY KEY DEFAULT uuid_generate_v4(),

	-- Order được áp dụng coupon
	"order_id" uuid NOT NULL,

	-- Coupon đã dùng
	"coupon_id" uuid NOT NULL,

	-- Cột dư / chưa rõ mục đích
	"id" uuid NOT NULL,

	-- Số tiền thực tế được giảm
	"applied_amount" numeric(19, 2)
);



-- =========================================================
-- ORDER PAYMENTS
-- =========================================================
-- Thông tin thanh toán của order
--
-- Online payment:
-- PENDING -> PAID
--
-- COD:
-- có thể PAID sau khi giao thành công
-- =========================================================

CREATE TABLE "order_payments" (

	-- ID payment
	"id" uuid DEFAULT uuid_generate_v4(),

	-- Order cần thanh toán
	"order_id" uuid NOT NULL
	CONSTRAINT "payments_order_id_unique" UNIQUE,

	-- Legacy field
	-- nên thay bằng payment_method_id
	"method" integer NOT NULL,

	-- Trạng thái thanh toán
	"status" varchar(255) DEFAULT 'PENDING',

	-- Thời gian thanh toán thành công
	"paid_at" timestamp,

	-- Payment method
	"payment_method_id" uuid,

	CONSTRAINT "payments_pkey"
	PRIMARY KEY("id")
);