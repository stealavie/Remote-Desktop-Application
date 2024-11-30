# Ứng dụng điều khiển từ xa

# Hướng dẫn Cài Đặt và Chạy Ứng Dụng

## Yêu Cầu
- **Java 17+**: Ứng dụng yêu cầu sử dụng Java 17 hoặc phiên bản mới hơn.
- **Eclipse IDE**: Eclipse là môi trường phát triển tích hợp (IDE) cho Java. Bạn sẽ sử dụng Eclipse để chỉnh sửa mã nguồn và chạy ứng dụng.

## Hướng Dẫn Cài Đặt và Chạy Ứng Dụng

### Bước 1: Cài Đặt Java 17+
1. Truy cập trang chính của [Oracle](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) hoặc [OpenJDK](https://jdk.java.net/17/) để tải xuống phiên bản Java 17+.
2. Sau khi tải về, tiến hành cài đặt Java theo hướng dẫn trên màn hình.
3. Để kiểm tra Java đã được cài đặt thành công, mở cửa sổ dòng lệnh (Command Prompt hoặc Terminal) và gõ:
java --version

4. Nếu Java đã được cài đặt thành công, bạn sẽ thấy phiên bản Java được hiển thị.

### Bước 2: Cài Đặt Eclipse IDE
1. Truy cập trang tải Eclipse tại [https://www.eclipse.org/downloads/](https://www.eclipse.org/downloads/).
2. Chọn "Eclipse IDE for Java Developers" và tải về phiên bản tương thích với hệ điều hành của bạn.
3. Sau khi tải xong, giải nén và mở Eclipse. Trong lần đầu mở, Eclipse sẽ yêu cầu bạn chọn một workspace (thư mục chứa các project của bạn). Bạn có thể chọn bất kỳ thư mục nào trên máy tính.
4. Kiểm tra cài đặt Eclipse bằng cách mở Eclipse và xem liệu IDE có nhận diện đúng Java hay không bằng cách vào **Window -> Preferences -> Java -> Installed JREs**.

### Bước 3: Clone Project từ GitHub
1. Mở Eclipse, vào menu **File -> Import**.
2. Chọn **Git -> Projects from Git**, sau đó nhấn **Next**.
3. Trong cửa sổ tiếp theo, chọn **Clone URI** và dán URL của repository GitHub mà bạn muốn clone. Ví dụ:
https://github.com/stealavie/Remote-Desktop-Application.git

4. Tiếp theo, chọn nhánh mà bạn muốn clone (thường là `main` hoặc `master`).
5. Chọn thư mục đích để lưu project trên máy tính và nhấn **Finish**.

### Bước 4: Import Project vào Eclipse
1. Sau khi đã clone project thành công, vào **File -> Import** trong Eclipse.
2. Chọn **General -> Existing Projects into Workspace**, nhấn **Next**.
3. Chọn thư mục chứa project vừa clone và nhấn **Finish**. Eclipse sẽ tự động nhận diện project và hiển thị nó trong workspace.

### Bước 5: Chạy Server trong Class `MainServer.java`
1. Trong **Project Explorer**, mở thư mục chứa mã nguồn Java.
2. Tìm và mở file `MainServer.java`.
3. Để chạy server, nhấp chuột phải vào class `MainServer.java` và chọn **Run As -> Java Application**.
4. Sau khi chạy, server sẽ bắt đầu lắng nghe các kết nối từ phía client.

### Bước 6: Chạy GUI trong `MainGUI.java`
1. Quay lại **Project Explorer**, tìm file `MainGUI.java`.
2. Mở file và thực hiện một cú nhấp chuột phải vào class `MainGUI.java`, sau đó chọn **Run As -> Java Application**.
3. Giao diện người dùng (GUI) sẽ mở ra và bạn có thể tương tác với ứng dụng thông qua các chức năng trên GUI.

## Lưu Ý
- Đảm bảo rằng Java 17+ và Eclipse IDE đã được cài đặt đúng cách để tránh các lỗi trong quá trình phát triển và chạy ứng dụng.
- Nếu gặp lỗi trong quá trình clone hoặc import project, hãy kiểm tra lại kết nối internet hoặc cài đặt Git trên hệ thống của bạn.

Chúc bạn thành công!
