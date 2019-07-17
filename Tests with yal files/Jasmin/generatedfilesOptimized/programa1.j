.class public programa1
.super java/lang/Object
.field public static mn I 
.field public static data [I 
.field public static mx I 

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method static public <clinit>()V
ldc 100
newarray int
putstatic programa1/data [I
return
.end method

.method public static det([I)V
.limit stack 25
.limit locals 25
B1:
iconst_0
istore 3
aload_0
arraylength
iconst_1
isub
istore 4
block0loop:
iload_3
iload 4
if_icmplt block0branch
goto block2
block0branch:
block1:
aload_0
iload_3
iaload
istore 1
iload_3
iconst_1
iadd
istore 3
aload_0
iload_3
iaload
istore 2
iload_1
iload_2
invokestatic library1/max(II)I
putstatic programa1/mx I
iload_1
iload_2
invokestatic library1/min(II)I
putstatic programa1/mn I
iload_3
iload 4
if_icmplt block0branch
block2:
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 5
.limit locals 5
B1:
getstatic programa1/data [I
invokestatic programa1/det([I)V
ldc "max: "
getstatic programa1/mx I
invokestatic io/println(Ljava/lang/String;I)V
ldc "min: "
getstatic programa1/mn I
invokestatic io/println(Ljava/lang/String;I)V
return
.end method

