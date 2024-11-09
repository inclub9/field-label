# Field Label

ไลบรารี Java annotation processor ที่สร้างคลาส constant สำหรับป้ายกำกับฟิลด์โดยอัตโนมัติ ช่วยจัดการป้ายกำกับฟิลด์แบบรวมศูนย์และให้การเข้าถึงที่ปลอดภัยตามประเภทข้อมูล

## คุณสมบัติ

- สร้างคลาส constant จาก field annotations โดยอัตโนมัติ
- แปลงชื่อฟิลด์จาก camelCase เป็น UPPER_CASE สำหรับค่าคงที่
- รองรับ Java 22
- ลงทะเบียน processor อัตโนมัติ (ไม่ต้องสร้างไฟล์ `javax.annotation.processing.Processor`)

## การติดตั้ง

เพิ่ม dependency ต่อไปนี้ในไฟล์ `pom.xml`:

```xml
<dependency>
    <groupId>com.github.inclub9</groupId>
    <artifactId>field-label</artifactId>
    <version>1.0.7</version>
</dependency>
```

## วิธีการใช้งาน

### 1. กำหนด Annotation ให้กับฟิลด์ในเอนทิตี้

```java
public class User {
    @FieldLabel("ชื่อผู้ใช้")
    private String username;

    @FieldLabel("รหัสผ่าน")
    private String password;

    @FieldLabel("ที่อยู่อีเมล")
    private String email;

    // getters และ setters
}
```

### 2. คอมไพล์โค้ด

หลังจากคอมไพล์ processor จะสร้างคลาสใหม่โดยเพิ่มคำว่า "Label" ต่อท้าย ตัวอย่างเช่น สำหรับคลาส `User` ข้างต้น จะได้:

```java
public final class UserLabel {
    private UserLabel() {}

    public static final String USERNAME = "ชื่อผู้ใช้";
    public static final String PASSWORD = "รหัสผ่าน";
    public static final String EMAIL = "ที่อยู่อีเมล";
}
```

### 3. การใช้งานค่าคงที่ที่สร้างขึ้น

```java
public class UserService {
    public void validateUser(User user) {
        if (user.getUsername() == null) {
            throw new ValidationException(UserLabel.USERNAME + " จำเป็นต้องระบุ");
        }
    }

    public void displayUserInfo(User user) {
        System.out.println(UserLabel.USERNAME + ": " + user.getUsername());
        System.out.println(UserLabel.EMAIL + ": " + user.getEmail());
    }
}
```

## ประโยชน์

- เชื่อมโยงโค้ดกับคำศัพท์ทางธุรกิจ ช่วยให้นักพัฒนาและผู้มีส่วนได้ส่วนเสียทางธุรกิจสื่อสารเข้าใจตรงกัน
- ลดช่องว่างระหว่างการพัฒนาโค้ดและโดเมนทางธุรกิจ ทำให้การพัฒนาสอดคล้องกับความต้องการของผู้ใช้
- จัดการป้ายกำกับได้ที่จุดเดียว ทำให้ปรับเปลี่ยนตามความต้องการทางธุรกิจได้ง่าย
- ป้องกันการพิมพ์ผิดด้วยการตรวจสอบในขั้นตอนการคอมไพล์
- รองรับการทำระบบหลายภาษา (i18n) ได้อย่างมีประสิทธิภาพ
- ประหยัดเวลาพัฒนาโดยไม่ต้องสร้างและดูแลคลาสค่าคงที่เอง
- ทำให้โค้ดอ่านเข้าใจง่ายขึ้นด้วยการใช้คำศัพท์ที่ตรงกับความเข้าใจของทีมธุรกิจ
- สร้างมาตรฐานการตั้งชื่อทั่วทั้งระบบ เพิ่มประสิทธิภาพการพัฒนาของทีม
- เข้าถึงป้ายกำกับอย่างปลอดภัยตามประเภทข้อมูล
- ปรับปรุงและบำรุงรักษาป้ายกำกับได้ง่าย

## แนวทางปฏิบัติที่ดี

1. อย่าลืมคอมไพล์ใหม่หลังจากเปลี่ยนชื่อฟิลด์หรือป้ายกำกับ
2. อัปเดตการอ้างอิงเมื่อเปลี่ยนชื่อฟิลด์
3. ใช้ป้ายกำกับที่มีความหมายและสอดคล้องกันทั่วทั้งแอปพลิเคชัน

## ความต้องการของระบบ

- Java 22 หรือสูงกว่า
- Maven 3.x

## การ Build จากซอร์สโค้ด

```bash
mvn clean install
```

## การมีส่วนร่วมพัฒนา

1. Fork repository
2. สร้าง feature branch (`git checkout -b feature/amazing-feature`)
3. Commit การเปลี่ยนแปลง (`git commit -m 'Add some amazing feature'`)
4. Push ไปยัง branch (`git push origin feature/amazing-feature`)
5. เปิด Pull Request

## ลิขสิทธิ์

โปรเจคนี้อยู่ภายใต้ MIT License - ดูรายละเอียดเพิ่มเติมได้ที่ไฟล์ LICENSE

## การสนับสนุน

สำหรับปัญหาและคำขอฟีเจอร์ใหม่ กรุณาใช้ GitHub issue tracker

## กิตติกรรมประกาศ

- พัฒนาด้วย [Google Auto Service](https://github.com/google/auto/tree/main/service) สำหรับการลงทะเบียน annotation processor