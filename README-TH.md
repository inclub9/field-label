# Field Label

ไลบรารี่ตัวประมวลผลแอนโนเทชั่น (Annotation Processor) สำหรับ Java ที่สร้างคลาสค่าคงที่สำหรับป้ายกำกับฟิลด์โดยอัตโนมัติ ช่วยจัดการป้ายกำกับฟิลด์แบบรวมศูนย์และให้การเข้าถึงที่ปลอดภัยด้วยการตรวจสอบประเภทข้อมูล

## คุณสมบัติหลัก

- สร้างค่าคงที่ 2 รูปแบบ:
   - รูปแบบชื่อฟิลด์ดั้งเดิม (camelCase)
   - รูปแบบตัวพิมพ์ใหญ่พร้อมขีดล่าง (UPPER_CASE)
- มีค่าคงที่ชื่อคลาสสำหรับการอ้างอิงที่สะดวก
- รองรับ Java 22
- ไม่ต้องตั้งค่าใดๆ ด้วยการลงทะเบียนตัวประมวลผลอัตโนมัติ
- ประมวลผลแบบ Thread-safe ด้วย concurrent collections
- ประหยัดหน่วยความจำด้วยการใช้ string builder ที่เหมาะสม

## การติดตั้ง

เพิ่มโค้ดต่อไปนี้ใน `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.inclub9</groupId>
    <artifactId>field-label</artifactId>
    <version>2.5.3</version>
</dependency>
```

## วิธีการใช้งาน

### 1. กำหนดแอนโนเทชั่นให้กับฟิลด์

```java
public class User {
    @FieldLabel("ชื่อผู้ใช้")
    private String username;

    @FieldLabel("ที่อยู่อีเมล")
    private String emailAddress;
    
    // getters และ setters
}
```

### 2. ผลลัพธ์ที่ถูกสร้าง

หลังจากคอมไพล์ จะมีการสร้างคลาสใหม่โดยเพิ่มคำว่า "Label" ต่อท้ายโดยอัตโนมัติ:

```java
public final class UserLabel {
    public static final String CLASS_NAME = "User";
    
    private UserLabel() {}

    // ชื่อฟิลด์ดั้งเดิม
    public static final String username = "ชื่อผู้ใช้";
    public static final String emailAddress = "ที่อยู่อีเมล";

    // ค่าคงที่แบบตัวพิมพ์ใหญ่
    public static final String USERNAME = "ชื่อผู้ใช้";
    public static final String EMAIL_ADDRESS = "ที่อยู่อีเมล";
}
```

### 3. การใช้งานค่าคงที่

```java
public class UserService {
    // ใช้รูปแบบชื่อฟิลด์ดั้งเดิม
    public void validateUser(User user) {
        if (user.getUsername() == null) {
            throw new ValidationException(UserLabel.username + " จำเป็นต้องระบุ");
        }
    }

    // ใช้รูปแบบตัวพิมพ์ใหญ่
    public Map<String, String> toMap(User user) {
        Map<String, String> map = new HashMap<>();
        map.put(UserLabel.USERNAME, user.getUsername());
        map.put(UserLabel.EMAIL_ADDRESS, user.getEmailAddress());
        return map;
    }
}
```

## ประโยชน์หลัก

1. **การเชื่อมโยงระหว่างธุรกิจและโค้ด**
   - รักษาความสอดคล้องระหว่างโค้ดและคำศัพท์ทางธุรกิจ
   - จัดการป้ายกำกับแบบรวมศูนย์
   - สนับสนุนการสื่อสารที่ชัดเจนระหว่างทีมเทคนิคและทีมธุรกิจ

2. **ประสิทธิภาพในการพัฒนา**
   - ไม่ต้องสร้างคลาสค่าคงที่ด้วยตนเอง
   - มีความปลอดภัยในการตรวจสอบประเภทข้อมูลตอนคอมไพล์
   - ลดความไม่สอดคล้องในการตั้งชื่อ
   - รองรับการปรับปรุงคำศัพท์ทางธุรกิจได้อย่างรวดเร็ว

3. **ความยืดหยุ่น**
   - รองรับทั้งรูปแบบ camelCase และ UPPER_CASE
   - บูรณาการกับเฟรมเวิร์ก i18n ได้ง่าย
   - ประมวลผลแบบ Thread-safe สำหรับโค้ดเบสขนาดใหญ่

## แนวทางปฏิบัติที่ดี

1. **การตั้งชื่อ**
   - ใช้ป้ายกำกับที่ชัดเจนและตรงกับคำศัพท์ทางธุรกิจ
   - รักษาความสอดคล้องในการจัดรูปแบบป้ายกำกับสำหรับฟิลด์ที่เกี่ยวข้องกัน
   - พิจารณาความต้องการด้าน i18n เมื่อเลือกป้ายกำกับ

2. **การจัดระเบียบโค้ด**
   - จัดกลุ่มฟิลด์ที่เกี่ยวข้องไว้ด้วยกัน
   - ใช้ชื่อฟิลด์ที่สื่อความหมายและสะท้อนวัตถุประสงค์
   - เขียนเอกสารกำกับสำหรับข้อกำหนดพิเศษหรือข้อตกลงในการใช้ป้ายกำกับ

3. **การบำรุงรักษา**
   - คอมไพล์ใหม่หลังจากเปลี่ยนแปลงแอนโนเทชั่นของฟิลด์
   - อัปเดตการอ้างอิงทั้งหมดเมื่อเปลี่ยนชื่อฟิลด์
   - ตรวจสอบคลาสที่ถูกสร้างเพื่อให้แน่ใจว่าได้ผลลัพธ์ตามที่ต้องการ

## ความต้องการของระบบ

- Java 22 หรือสูงกว่า
- Maven 3.x

## การ Build

```bash
mvn clean install
```

## การมีส่วนร่วมพัฒนา

1. Fork repository
2. สร้าง feature branch (`git checkout -b feature/คุณสมบัติใหม่`)
3. Commit การเปลี่ยนแปลง (`git commit -m 'เพิ่มคุณสมบัติใหม่'`)
4. Push ไปยัง branch (`git push origin feature/คุณสมบัติใหม่`)
5. เปิด Pull Request

## การสนับสนุน

สำหรับปัญหาและคำขอคุณสมบัติใหม่ กรุณาใช้ [GitHub issue tracker](https://github.com/inclub9/field-label/issues)

## ลิขสิทธิ์

โครงการนี้อยู่ภายใต้ MIT License - ดูรายละเอียดได้ที่ไฟล์ [LICENSE](LICENSE)

## กิตติกรรมประกาศ

- [Google Auto Service](https://github.com/google/auto/tree/main/service) สำหรับการลงทะเบียนตัวประมวลผลแอนโนเทชั่น
- ผู้มีส่วนร่วมและผู้ใช้งานที่ให้ข้อเสนอแนะที่มีคุณค่า