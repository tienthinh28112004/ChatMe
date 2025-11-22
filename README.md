## [Chat me]

## MÔ TẢ HỆ THỐNG

Hệ thống **ChatMe** được xây dựng theo mô hình **client–server nhiều tầng**, với mục tiêu hỗ trợ nhắn tin realtime, ổn định và dễ mở rộng.s

Phía **client** là ứng dụng web viết bằng **ReactJS**. Toàn bộ lưu lượng mạng từ client được điều hướng thông qua **Nginx**, đóng vai trò *gateway* và *reverse proxy*. Tại đây, Nginx phân chia luồng dữ liệu:

- Các request REST như `/api/...` được chuyển tiếp đến **backend Spring Boot**.
- Các kết nối WebSocket tại `/connection/websocket` được proxy trực tiếp đến **Centrifugo**, server chuyên xử lý realtime.

**Backend Spring Boot** đảm nhiệm toàn bộ phần nghiệp vụ của hệ thống: xác thực người dùng, quản lý phòng chat, xử lý gửi/nhận tin nhắn, upload file và lưu trữ dữ liệu vào **MySQL**. Khi có tin nhắn mới hoặc sự kiện realtime, backend sử dụng **HTTP API** để publish sự kiện sang Centrifugo.

**Centrifugo** là thành phần chịu trách nhiệm realtime. Centrifugo sử dụng **Redis** làm *Pub/Sub engine* để quản lý kết nối WebSocket, trạng thái người dùng (presence), danh sách subscriber và phân phối tin nhắn đến các client đang kết nối. Nhờ kiến trúc dựa trên Go và mô hình pub/sub qua Redis, Centrifugo có khả năng xử lý đồng thời **hàng nghìn kết nối WebSocket và lượng sự kiện lớn** mà không ảnh hưởng đến backend, giúp đảm bảo độ trễ thấp và tính ổn định trong quá trình trao đổi tin nhắn. 

Việc tích hợp **Redis** giúp Centrifugo dễ dàng **mở rộng theo chiều ngang (horizontal scaling)**. Thay vì phụ thuộc vào một server duy nhất, hệ thống có thể triển khai **nhiều instance Centrifugo** chạy song song, tạo khả năng mở rộng linh hoạt và sẵn sàng cho việc phát triển, nâng cấp kiến trúc trong tương lai.

Toàn bộ các thành phần của hệ thống gồm **Frontend React**, **Backend Spring Boot**, **Nginx**, **Centrifugo** và **Redis** đều được triển khai dưới dạng container độc lập và điều phối bằng **Docker Compose**, giúp môi trường phát triển và triển khai trở nên nhất quán, dễ chạy và dễ bảo trì.

**Cấu trúc logic tổng quát:**
```
Client (ReactJS) <--> Nginx <--> Server (Spring Boot) <--> Database / External Services
        ↑                                       |
        |                                       |
        └──────── Realtime từ Centrifugo <------┘
 
```

**Sơ đồ hệ thống:**

![System Diagram](./statics/diagram.png)

---

## CÔNG NGHỆ SỬ DỤNG

| Thành phần | Công nghệ | Ghi chú |
|------------|-----------|---------|
| Server | Java Maven 3.4.1 + Spring boot | Upstream server,REST API |
| Client | ReactJs 19.2.0 + Axios | Giao tiếp HTTP,WebSocket client |
| Gateway | Nginx | Chuyển tiếp HTTP/WS,Reverse proxy|
| Realtime| Centrifugo Pro 6.4.0| WebSocket server |
| Database | MySql,Redis,Cloudinary | Lưu trữ dữ liệu |
| Triển khai | Docker | Mạng ảo |

---

## HƯỚNG DẪN CHẠY DỰ ÁN

### 1. Clone repository
```bash
git clone https://github.com/jnp2018/mid-project-829529302.git
cd mid-project-829529302
```

### 2. Chạy cấu hình dự án ban đầu
-Chạy docker trên máy sau đó chạy các lệnh
```bash
cd source
# Các lệnh để cấu hình dự án
docker compose up --build
```

### 3. Chạy dự án
```bash
cd source
# Các lệnh để khởi động dự án
docker compose up
```

### 4. Kiểm thử nhanh
```bash
# Các lệnh test
docker ps
```
-Nếu thấy các container backend,frontend,nginx,centrifugo,redis hiện lên là đã build thành công

---

## GIAO TIẾP (GIAO THỨC SỬ DỤNG)

| Endpoint | Protocol | Method | Input | Output |
|----------|----------|--------|--------|--------|
| `/api/auth/register` | HTTP/1.1 | POST | Body: UserCreateRequest | ApiResponse<UserResponse> |
| `/api/auth/login` | HTTP/1.1 | POST | Body: LogInRequest | ApiResponse<SignInResponse> |
| `/api/auth/outbound/authentication?code=...` | HTTP/1.1 | POST | Query: code | ApiResponse<SignInResponse> |
| `/api/auth/logout` | HTTP/1.1 | POST | Body: LogOutRequest | ApiResponse<String> |
| `/api/auth/refresh` | HTTP/1.1 | POST | Cookie: refreshToken | ApiResponse<RefreshTokenResponse> |
| `/api/auth/introspect` | HTTP/1.1 | POST | Body: IntrospectRequest | ApiResponse<IntrospectResponse> |
| `/api/user/getUserById/{userId}` | HTTP/1.1 | POST | Path: userId | ApiResponse<UserResponse> |
| `/api/user/getMyInfo` | HTTP/1.1 | GET | — | ApiResponse<UserResponse> |
| `/api/user/getUserByKeyword?keyword=...` | HTTP/1.1 | GET | Query: keyword | ApiResponse<List<UserResponse>> |
| `/api/centrifugo/connectionToken` | HTTP/1.1 | GET | — | ApiResponse<String> |
| `/api/centrifugo/subcriptionToken?channels=...` | HTTP/1.1 | GET | Query: channels | ApiResponse<String> |
| `/api/centrifugo/isUserOnline/{userId}` | HTTP/1.1 | GET | Path: userId | ApiResponse<Boolean> |
| `/api/centrifugo/getRoomOnlineCount/{roomId}` | HTTP/1.1 | GET | Path: roomId | ApiResponse<Integer> |
| `/api/centrifugo/notifyOnline` | HTTP/1.1 | POST | — | ApiResponse<String> |
| `/api/centrifugo/notifyOffline` | HTTP/1.1 | POST | — | ApiResponse<String> |
| `/api/uploadImage` | HTTP/1.1 | POST | multipart/form-data: file | ApiResponse<UploadResponse> |
| `/api/rooms/{roomId}/messages` | HTTP/1.1 | GET | Path: roomId | ApiResponse<List<MessageResponse>> |
| `/api/rooms/sendMessage` | HTTP/1.1 | POST | Body: MessageRequest | ApiResponse<MessageResponse> |
| `/api/room/listRoom` | HTTP/1.1 | GET | Query: keyword | ApiResponse<List<RoomResponse>> |
| `/api/room/addRoom` | HTTP/1.1 | POST | Body: CreateRoomRequest | ApiResponse<RoomResponse> |
| `/api/rooms/join/{roomId}` | HTTP/1.1 | POST | Path: roomId | ApiResponse<RoomMemberResponse> |
| `/api/rooms/leave/{roomId}` | HTTP/1.1 | POST | Path: roomId | ApiResponse<RoomMemberResponse> |


---

## KẾT QUẢ THỰC NGHIỆM

> Đưa ảnh chụp kết quả hoặc mô tả log chạy thử.
- Trang đăng ký
![Demo Result](./statics/RegisterPage.png)

- Trang đăng nhập
![Demo Result](./statics/LoginPage.png)

- Trang gửi tin nhắn cá nhân
![Demo Result](./statics/SendMessageDirect.png)

- Trang gửi tin nhắn nhóm
![Demo Result](./statics/SendMessageGroup.png)
---

## CẤU TRÚC DỰ ÁN

```
mid-project-829529302/
├── INSTRUCTION.md
└── README.md
├── statics
│   ├── diagram.png
│   └── logo.png
├── source
│   ├── client
│   │   ├── chatme
│   │   │   ├── public
│   │   │   ├── src
│   │   │   │   ├── components
│   │   │   │   │   ├── authentication
│   │   │   │   │   ├── config
│   │   │   │   │   ├── css
│   │   │   │   │   ├── error
│   │   │   │   │   └── pages
│   │   │   │   │       ├── ChatMe
│   │   │   │   │       │   ├── components
│   │   │   │   │       │   └── ChatMePage.jsx
│   │   │   │   │       ├── LoginPage
│   │   │   │   │       │   ├── components
│   │   │   │   │       │   └── LoginPage.jsx
│   │   │   │   │       └── RegisterPage
│   │   │   │   │           ├── components
│   │   │   │   │           └── RegisterPage.jsx
│   │   │   │   ├── context
│   │   │   │   ├── hooks
│   │   │   │   ├── service
│   │   │   │   ├── utils
│   │   │   │   ├── App.css
│   │   │   │   ├── App.js
│   │   │   │   ├── App.test.js
│   │   │   │   ├── index.css
│   │   │   │   ├── index.js
│   │   │   │   ├── setupTests.js
│   │   │   │   └── store.js
│   │   │   ├── .gitignore
│   │   │   ├── Dockerfile
│   │   │   ├── README.md
│   │   │   ├── package-lock.json
│   │   │   ├── package.json
│   │   └── README.md
│   ├── server
│   │   ├── WeChat
│   │   │   ├── src
│   │   │   │   ├── main
│   │   │   │   │   ├── java
│   │   │   │   │   │   └── LapTrinhMang
│   │   │   │   │   │       └── WeChat
│   │   │   │   │   │           ├── Configuration
│   │   │   │   │   │           ├── Controller
│   │   │   │   │   │           ├── Dto
│   │   │   │   │   │           ├── Entity
│   │   │   │   │   │           ├── Enums
│   │   │   │   │   │           ├── Exception
│   │   │   │   │   │           ├── Repository
│   │   │   │   │   │           ├── Service
│   │   │   │   │   │           ├── Utils
│   │   │   │   │   │           ├── Validator
│   │   │   │   │   │           └── WeChatApplication.java
│   │   │   │   │   └── resources
│   │   │   │   │       └── application.yaml
│   │   │   ├── .gitattributes
│   │   │   ├── Dockerfile
│   │   │   ├── docker-compose.dev.test.yml
│   │   │   ├── mvnw
│   │   │   ├── mvnw.cmd
│   │   │   └── pom.xml
│   │   └── README.md
│   ├── nginx
│   │   └── nginx.conf
│   ├── centrifugo
│   │   ├── config.json
│   └── docker-compose.yml
├── .gitignore
```

---

## HƯỚNG PHÁT TRIỂN THÊM

> Ý tưởng mở rộng hoặc cải tiến hệ thống.
- [ ] **Tối ưu hiệu năng:** giảm khóa DB khi nhiều người gửi tin cùng lúc, bổ sung cơ chế retry hoặc optimistic locking để tránh xung đột dữ liệu.
- [ ] **Mở rộng realtime:** hỗ trợ chạy nhiều node Centrifugo, tối ưu presence, thêm tính năng typing indicator và đảm bảo không mất tin nhắn khi server restart.
- [ ] **Scale hệ thống:** nhân bản backend và cấu hình Nginx load balancing; tách lưu trữ file sang dịch vụ riêng (S3/MinIO).
- [ ] **Tăng cường bảo mật:** thêm rate limiting, nâng cấp bảo vệ API và hỗ trợ xác thực hai lớp (2FA).
- [ ] **Bổ sung tính năng:** tìm kiếm tin nhắn, thông báo (notification), và tích hợp gọi thoại/video bằng WebRTC.
