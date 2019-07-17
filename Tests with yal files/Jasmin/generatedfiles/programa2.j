.class public programa2
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f1([I)[I
.limit stack 20
.limit locals 20
B1:
iconst_0
istore 2
aload_0
arraylength
istore 1
iload_1
newarray int
astore 1
block0loop:
iload_2
aload_0
arraylength
if_icmplt block0branch
goto block2
block0branch:
block1:
aload_0
iload_2
iaload
istore 4
aload 1
iload_2
iload 4
iastore
iload_2
iconst_1
iadd
istore 2
iload_2
aload_0
arraylength
if_icmplt block0branch
block2:
aload 1
areturn
.end method

.method public static f2(I)[I
.limit stack 10
.limit locals 10
B1:
iload_0
newarray int
astore 0
aload_0
arraylength
istore 2
ldc 0
istore 3
block4loopstart:
iload 3
iload 2
if_icmplt block4body
goto block4end
block4body:
aload_0
iload 3
iconst_1
iastore
iload 3
ldc 1
iadd
istore 3
goto block4loopstart
block4end:
aload 0
areturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 25
.limit locals 25
B1:
bipush 100
newarray int
astore 3
iconst_1
istore 5
aload 3
iconst_0
iload 5
iastore
iconst_2
istore 5
aload 3
bipush 99
iload 5
iastore
aload_3
invokestatic programa2/f1([I)[I
astore 2
aload_2
iconst_0
iaload
istore 4
aload_2
bipush 99
iaload
istore 3
ldc "first: "
iload 4
invokestatic io/println(Ljava/lang/String;I)V
ldc "last: "
iload_3
invokestatic io/println(Ljava/lang/String;I)V
bipush 100
invokestatic programa2/f2(I)[I
astore 2
aload_2
iconst_0
iaload
istore 4
aload_2
bipush 99
iaload
istore 3
ldc "first: "
iload 4
invokestatic io/println(Ljava/lang/String;I)V
ldc "last: "
iload_3
invokestatic io/println(Ljava/lang/String;I)V
return
.end method

