.class public callMain
.super java/lang/Object
.field public static x I = 1

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f()V
.limit stack 10
.limit locals 10
B1:
block0:
getstatic callMain/x I
iconst_0
if_icmpgt block0true
goto block2
block0true:
block1:
getstatic callMain/x I
iconst_1
isub
putstatic callMain/x I
ldc 1
anewarray java/lang/String
invokestatic callMain/main([Ljava/lang/String;)V
block2:
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 15
.limit locals 15
B1:
ldc "Call main"
invokestatic io/println(Ljava/lang/String;)V
invokestatic callMain/f()V
return
.end method

