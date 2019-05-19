;;stdio::print_int:VOID(input:INT)

.method public static stdio__print_int(I)V
	.limit stack 2
	.limit locals 1
	
	getstatic java/lang/System/out Ljava/io/PrintStream;
	iload 0
	invokevirtual java/io/PrintStream/println(I)V
	
	return
.end method

;! end print_int

;;stdio::print_bool:VOID(input:BOOLEAN)

.method public static stdio__print_bool(Z)V
	.limit stack 2
	.limit locals 1
	
	getstatic java/lang/System/out Ljava/io/PrintStream;
	iload 0
	invokevirtual java/io/PrintStream/println(Z)V
	
	return
.end method

;! end print_bool