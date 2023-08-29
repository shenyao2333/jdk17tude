#### jdk11引入新的字符串API

1、`isBlank()` 方法 ，检查字符串是否为空白字符串（全由空格组成或者长度为0）

```java
String str = "   ";
boolean isBlank = str.isBlank(); // true
```



2、`strip()` 方法：移除字符串首尾的空白字符。

```java
String str = "  Hello, World!  ";
String stripped = str.strip(); // "Hello, World!"
```

3、`stripLeading()` 方法：移除字符串开头的空白字符。

```java
String str = "  Hello, World!";
String stripped = str.stripLeading(); // "Hello, World!"
```

4、`stripTrailing()` 方法：移除字符串末尾的空白字符。

```java
String str = "Hello, World!  ";
String stripped = str.stripTrailing(); // "Hello, World!"
```

5、`repeat()` 方法：重复指定次数的字符串拼接。

```java
String str = "Java ";
String repeated = str.repeat(3); // "Java Java Java "
```

