# MODULE CLIENT
---

## MỤC TIÊU

Client chịu trách nhiệm:
- Hiển thị giao diện người dùng (UI) và toàn bộ tương tác của người dùng.
- Gửi request HTTP đến Backend thông qua Axios (đăng nhập, đăng ký, gửi tin nhắn, tạo phòng…).
- Nhận và xử lý dữ liệu realtime từ Centrifugo thông qua WebSocket (tin nhắn mới, online/offline, sự kiện hệ thống).
- Quản lý trạng thái người dùng (token đăng nhập, thông tin cá nhân, phòng đang mở…).
- Thực hiện subscribe/unsubscribe các kênh realtime như room:<roomId> và personal:<userId>.
- Upload ảnh và file từ người dùng lên Backend → Cloudinary.
- Render danh sách phòng, danh sách user, danh sách tin nhắn theo thời gian thực.
- Quản lý điều hướng (routing) giữa các trang: login → chat.
- Hiển thị thông báo cho người dùng (toast thông báo lỗi/thành công).


---

## CÔNG NGHỆ SỬ DỤNG

| Thành phần        | Công nghệ                                      |
|-------------------|------------------------------------------------|
| Ngôn ngữ          | JavaScript (ES6+), JSX                         |
| Thư viện chính    | ReactJS, Axios, Centrifuge JS, React-Router   |
| Giao thức         | HTTP/1.1 (REST API), WebSocket (Realtime)     |

---

## HƯỚNG DẪN CHẠY

### Cài đặt
```bash
npm install
```

### Chạy chương trình
```bash
npm start
```

### Cấu hình (nếu cần)
- Server URL: `http://localhost:9000`
---

## CẤU TRÚC
```
client/
├── chatme
│   ├── public
│   ├── src
│   │   ├── components
│   │   │   ├── authentication
│   │   │   │   ├── HandleLogout.js
│   │   │   │   ├── OAuth2.js
│   │   │   │   └── UseAuth.js
│   │   │   ├── config
│   │   │   │   └── OAuthConfig.js
│   │   │   ├── css
│   │   │   │   ├── ChatHeader.css
│   │   │   │   ├── ChatInput.css
│   │   │   │   ├── ChatMessageList.css
│   │   │   │   ├── SearchRoom.css
│   │   │   │   └── ViewRoom.css
│   │   │   ├── error
│   │   │   │   ├── Accessdenied.js
│   │   │   │   └── NotFound.js
│   │   │   └── pages
│   │   │       ├── ChatMe
│   │   │       │   ├── components
│   │   │       │   │   ├── ChatHeader.jsx
│   │   │       │   │   ├── ChatInput.jsx
│   │   │       │   │   ├── ChatMessageList.jsx
│   │   │       │   │   ├── SearchRoom.jsx
│   │   │       │   │   ├── ViewRoom.jsx
│   │   │       │   │   └── ViewUser.jsx
│   │   │       │   └── ChatMePage.jsx
│   │   │       ├── LoginPage
│   │   │       │   ├── components
│   │   │       │   │   └── LoginForm.jsx
│   │   │       │   └── LoginPage.jsx
│   │   │       └── RegisterPage
│   │   │           ├── components
│   │   │           │   └── RegisterForm.jsx
│   │   │           └── RegisterPage.jsx
│   │   ├── context
│   │   │   └── AuthContext.jsx
│   │   ├── hooks
│   │   │   └── useAuthData.js
│   │   ├── service
│   │   │   ├── AuthenticationService.js
│   │   │   ├── CentrifugoService.js
│   │   │   ├── ImageService.js
│   │   │   ├── MessageService.js
│   │   │   ├── RoomMemberService.js
│   │   │   ├── RoomService.js
│   │   │   └── UserService.js
│   │   ├── utils
│   │   │   ├── CustomizeAxios.js
│   │   │   └── Loading.jsx
│   │   ├── App.css
│   │   ├── App.js
│   │   ├── App.test.js
│   │   ├── index.css
│   │   ├── index.js
│   │   ├── logo.svg
│   │   ├── reportWebVitals.js
│   │   ├── setupTests.js
│   │   └── store.js
│   ├── .gitignore
│   ├── Dockerfile
│   ├── README.md
│   ├── package-lock.json
│   ├── package.json
│   └── yarn.lock
└── README.md
```


---

##  SỬ DỤNG
```bash
# Ví dụ gửi request
# Lấy subscription token cho một room
curl -X GET "http://localhost:9000/api/centrifugo/subcriptionToken?channels=room:123" \
     -H "Authorization: Bearer <access_token>"
```

---

## GHI CHÚ
- Thường chạy client bằng lệnh docker compose up
- Đảm bảo server đã chạy trước khi khởi động client
- Mặc định kết nối đến `localhost:9000`
