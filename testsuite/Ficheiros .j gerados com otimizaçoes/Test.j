.class public Test
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f1()V
.limit stack 10
.limit locals 10
B1:
return
.end method

.method public static f2(II)I
.limit stack 25
.limit locals 25
B1:
iconst_0
istore 2
block0:
iload_0
iload_1
if_icmpgt block0true
block2:
iconst_2
istore 2
goto block3
block0true:
block1:
iconst_1
istore 2
block3:
iload 2
ireturn
.end method

.method public static f3(II)I
.limit stack 25
.limit locals 25
B1:
iconst_0
istore 2
block4:
iload_0
iload_1
if_icmpeq block4true
block6:
iconst_2
istore 2
goto block7
block4true:
block5:
iconst_1
istore 2
block7:
iload 2
ireturn
.end method

.method public static f4(II)V
.limit stack 20
.limit locals 20
B1:
block8loop:
iload_0
iload_1
if_icmpgt block8branch
goto block10
block8branch:
block9:
ldc "a = "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
iload_0
iconst_1
isub
istore 0
iload_0
iload_1
if_icmpgt block8branch
block10:
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
invokestatic Test/f1()V
iconst_5
iconst_0
invokestatic Test/f2(II)I
istore 2
ldc "x = "
iload_2
invokestatic io/println(Ljava/lang/String;I)V
iconst_5
iconst_1
invokestatic Test/f3(II)I
istore 2
ldc "x = "
iload_2
invokestatic io/println(Ljava/lang/String;I)V
iconst_5
iconst_1
invokestatic Test/f4(II)V
return
.end method

