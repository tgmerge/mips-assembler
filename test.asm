direct 4096
addi $t1, $zero, 10
addi $t2, $zero, 10
beq  $t1, $t2, skip
add  $t3, $t1, $t2
skip:
add  $t3, $zero, $t1