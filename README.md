# Hướng Dẫn Cài Đặt và Chạy Ứng Dụng Remote Desktop Application

Trong tài liệu này, chúng tôi sẽ hướng dẫn chi tiết từng bước để thiết lập môi trường phát triển, chạy server, và giao diện người dùng (GUI). Hãy đảm bảo bạn đã cài đặt đầy đủ các công cụ cần thiết trước khi bắt đầu. Chúc bạn thành công!

---

## 1. **Yêu Cầu Hệ Thống**

### **1.1. Java 17+**
- Ứng dụng yêu cầu sử dụng Java 17 hoặc phiên bản mới hơn. Java là ngôn ngữ lập trình chính cho ứng dụng này.  

### **1.2. Công cụ lập trình**
- **Eclipse IDE** hoặc **Visual Studio Code (VS Code)**: Bạn có thể sử dụng bất kỳ IDE hoặc text editor nào để phát triển ứng dụng. Trong hướng dẫn này, chúng tôi sẽ sử dụng Eclipse IDE và VS Code.  

---

## 2. **Hướng Dẫn Cài Đặt và Chạy Ứng Dụng**

### **Bước 1: Cài Đặt Java 17+**

1. **Tải và cài đặt Java 17+:**
   - Truy cập trang chính thức để tải xuống:
     - Oracle JDK: [Tải tại đây](https://www.oracle.com/java/technologies/javase-downloads.html)
     - OpenJDK: [Tải tại đây](https://openjdk.org)
   - Tiến hành cài đặt theo hướng dẫn trên màn hình.

2. **Kiểm tra cài đặt Java:**
   - Mở Command Prompt hoặc Terminal và chạy lệnh:  
     ```bash
     java --version
     ```
   - Nếu Java được cài đặt thành công, bạn sẽ thấy phiên bản Java hiển thị.

---

### **Bước 2: Cài Đặt Môi Trường Lập Trình**

#### **2.1. Cài Đặt Eclipse IDE**
- Truy cập [trang tải Eclipse](https://www.eclipse.org/downloads/).  
- Tải về phiên bản **Eclipse IDE for Java Developers** phù hợp với hệ điều hành của bạn.  
- Giải nén và mở Eclipse. Lần đầu mở, Eclipse sẽ yêu cầu chọn **workspace** (thư mục chứa các project).  
- Đảm bảo Eclipse nhận diện đúng JDK bằng cách kiểm tra tại:  
  **Menu Window -> Preferences -> Java -> Installed JREs**.  

#### **2.2. Cài Đặt Visual Studio Code**
- Truy cập [trang tải Visual Studio Code](https://code.visualstudio.com/).  
- Tải và cài đặt phiên bản phù hợp với hệ điều hành.  
- Sau khi cài đặt, mở **Extensions** (hoặc nhấn `Ctrl + Shift + X`) và tìm, cài đặt:  
  - **Java Extension Pack** (bao gồm các công cụ cần thiết để phát triển Java).

---

### **Bước 3: Clone Project từ GitHub**

#### **3.1. Sử dụng Eclipse**
- Mở Eclipse, vào menu:  
  **File -> Import -> Git -> Projects from Git** -> **Next**.  
- Chọn **Clone URI** và nhập URL repository GitHub:  
  ```plaintext
  https://github.com/stealavie/Remote-Desktop-Application.git
  ```
- Chọn nhánh (branch) và nhấn **Next**.  
- Chọn thư mục đích trên máy tính để lưu project và nhấn **Finish**.

#### **3.2. Sử dụng Visual Studio Code**
- Mở VS Code và vào **View -> Terminal** (hoặc nhấn `Ctrl + ~`).  
- Gõ lệnh:  
  ```bash
  git clone https://github.com/stealavie/Remote-Desktop-Application.git
  ```
- Sau khi clone, mở thư mục project trong VS Code qua:  
  **File -> Open Folder**.

---

### **Bước 4: Import Project vào IDE**

#### **4.1. Sử dụng Eclipse**
- Vào menu **File -> Import -> General -> Existing Projects into Workspace** -> **Next**.  
- Chọn thư mục chứa project đã clone và nhấn **Finish**.

#### **4.2. Sử dụng Visual Studio Code**
- Mở thư mục project trong VS Code.  
- Nếu cần, cài thêm **Java Projects Extension** để nhận diện cấu trúc dự án.  
- Chạy lệnh: **Terminal -> Run Build Task** (hoặc `Ctrl + Shift + B`) để xây dựng dự án.

---

### **Bước 5: Chạy Server trong MainServer.java**

#### **5.1. Sử dụng Eclipse**
- Trong **Project Explorer**, tìm và mở file **MainServer.java**.  
- Nhấp chuột phải, chọn **Run As -> Java Application**.  
- Server sẽ bắt đầu lắng nghe các kết nối từ client.

#### **5.2. Sử dụng Visual Studio Code**
- Mở file **MainServer.java**.  
- Nhấn `Ctrl + Shift + P`, chọn **Java: Run**.  
- Server sẽ chạy và lắng nghe kết nối.

---

### **Bước 6: Chạy GUI trong RemoteDesktop.java**

#### **6.1. Sử dụng Eclipse**
- Tìm và mở file **RemoteDesktop.java** trong **Project Explorer**.  
- Nhấp chuột phải, chọn **Run As -> Java Application**.  
- Giao diện người dùng (GUI) sẽ hiển thị và bạn có thể tương tác.

#### **6.2. Sử dụng Visual Studio Code**
- Mở file **RemoteDesktop.java**.  
- Nhấn `Ctrl + Shift + P`, chọn **Java: Run**.  
- GUI sẽ được mở để bạn sử dụng.

---

## **Lưu Ý Quan Trọng**
- Đảm bảo **JDK 17+** được cấu hình chính xác trong IDE của bạn.  
- Nếu sử dụng **Maven** hoặc **Gradle**, hãy kiểm tra và tải đầy đủ các thư viện phụ thuộc (dependencies).  

---

**Cảm ơn bạn đã theo dõi hướng dẫn!**  
Chúc các bạn may mắn!!
