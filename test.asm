lui        $fp, 12
lui     $sp, 12

	jal        	asm04
add     $zero, $zero, $zero

pause:
j         pause
add     $zero, $zero, $zero

asm04:
addiu    $sp, $sp, -4
sw         $t0, 0($sp)

lw         $t0, 0($zero)
srl        $t0, $t0, 8
addi     $t0, $t0, 1    
sll     $t0, $t0, 8
sw         $t0, 0($zero)

lw         $t0, 0($sp)
addiu    $sp, $sp, 4

jr         $ra
add     $zero, $zero, $zero
