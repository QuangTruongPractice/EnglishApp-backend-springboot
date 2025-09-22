# EnglishApp-backend-springboot
App học tiếng Anh với Flashcard tích hợp chấm điểm phát âm - Backend API
Mô tả dự án
Backend API cho hệ thống học tiếng Anh với Flashcard tích hợp chấm điểm phát âm

Tính năng chính

1.  Xác thực & Phân quyền

- Sử dụng **Spring Security** để quản lý đăng nhập và phân quyền.
- Tích hợp **JWT**:
  - Khi đăng nhập thành công, hệ thống sinh **Access Token**.
  - Token được dùng để truy cập API bảo mật.
- Phân quyền:
  - **ADMIN**: Quản trị toàn bộ hệ thống (truy cập trang quản trị).
  - **User**: Người dùng thực tế.
  - Người dùng thông thường **không thể truy cập trang quản trị**.
- Google Oauth2: Có thể dùng Google để đăng nhập. 

2.  Quản lý User

- User:
  - Chỉnh sửa thông tin cá nhân, Upload avatar (lưu trữ trên **Cloudinary**).
  - Đăng ký tài khoản, đăng nhập bằng email/mật khẩu.
  - Đăng nhập nhanh bằng Google.
- Admin:
  - Quản lý toàn bộ user (xem, thêm, sửa, xóa).
  - Quản lý nội dung học tập: từ vựng, chủ đề, video, quiz.

3.  Quản lý Video

- Thêm video mới: Khi thêm video, hệ thống gọi API FastAPI để xử lý và lấy subtitle.
- Xóa video: Khi xóa video, toàn bộ subtitle liên quan cũng bị xóa theo.

4.  Quản lý Quiz

- Admin có thể:
  - Thêm, sửa, xóa Quiz.
  - Quản lý Answer cho từng quiz.

5.  Quản lý Từ vựng & Chủ đề

- Admin có thể:
  - Thêm, sửa, xóa Từ vựng.
  - Quản lý Chủ đề (Topic) và Chủ đề phụ (Subtopic).
    + Mỗi Chủ đề có nhiều Chủ đề phụ.
    + Mỗi Chủ đề phụ chứa nhiều Từ vựng.

6.  Thống kê dữ liệu

- API trả về dữ liệu thống kê theo:
- Có thể tích hợp Chart.js / Google Charts ở trang quản trị.
- Thống kê user trong hệ thống theo năm, tháng, quý.

Công nghệ sử dụng

- Ngôn ngữ: Java 21+
- Framework: Spring Boot
- Bảo mật: Spring Security, JWT
- View Engine: Thymeleaf
- CSDL: MySQL
- Lưu trữ ảnh: Cloudinary
- Email: Spring Mail (JavaMailSender)
- Kiểm tra dữ liệu: Bean Validation (Hibernate Validator)

Kiến trúc dự án
src/main/java/com/tqt

- conponents/ Xử lý tự động hóa bằng Scheduler
- config/ Cấu hình Security, Cloudinary, Mail
- controller/ Xử lý request từ quản trị & API
- dto/ Dữ liệu gửi đi và dữ liệu trả về
- entity/ Các bảng dữ liệu
- repository/ Tầng truy vấn dữ liệu (Repositories) với JPA
- service/ Xử lý nghiệp vụ (Services / MicroServices)
- filters/ Kiểm tra và xác thực JWT token trong mỗi request
- utils/ Xử lý generate token và claims token

Cài đặt & Chạy backend

1. Clone dự án

https://github.com/QuangTruongPractice/EnglishApp-backend-springboot.git

2. Cấu hình Database trong resources/application.yaml

IntelliJ IDEA
spring:
  datasource:
    url: "jdbc:mysql://localhost:3306/englishdb"
    username: root
    password: Admin@123
    driver-class-name: com.mysql.cj.jdbc.Driver

Có thể lấy database mẫu ở application.yaml ở github (đã chỉnh sửa và public trên Railway)
Cấu hình Mail ở MailConfig

3. Chạy ứng dụng
   Mở IntelliJ IDEA
   Build để tải các thư viện cần thiết ở pom.xml
   Run để chạy
4. Truy cập
   API: http://localhost:8080/api/...
   Trang quản trị (Thymeleaf): http://localhost:8080/admin/...

Backend Spring Boot hiện tại đã thực hiện deploy tại: https://englishapp-go7r.onrender.com/admin/

API Documentation

-> API Endpoints Chính

Authentication

- POST `/api/login` - Đăng nhập
- POST `/api/register` - Đăng ký user
- POST `/api/auth/google-signin` - Đăng ký user
Vocabulary

- GET `/api/vocabulary` - Lấy danh sách từ vựng
- GET `/api/sub-topics/{id}` - Lấy danh sách từ vựng trong 1 sub topic

Video
- GET `/api/videos` - Lấy danh sách video
- GET `/api/videos/{id}` - Lấy danh sách video và subtitle
 
Admin

- GET `/admin/` - Trang quản trị

Người thực hiện

1.  Trần Quang Trường

Contact

- Email: tranquangtruong25@gmail.com
- GitHub: https://github.com/QuangTruongPractice
