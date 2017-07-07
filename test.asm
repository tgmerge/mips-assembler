begin:
         lw $1, 20($0)         
         lw  $2, 21($0)        
         lw  $3, 22($0)        
         lw  $4, 23($0)        
         add $5, $2, $2       
         addiu $6, $2, -1      
         lui $7, 1024           
         xor  $7, $7, $4     
         slti $7, $7, -1       
         sltiu $7, $7, -1      
         beq $1, $2, skip1     
         addi $1, $0, 4        
         beq $1, $3, skip1     
         addi $0, $0, 0        
skip1:
         addi $1, $0, 8        
skip3:
         beq $0, $0, skip2       
skip2:
         beq $0, $0, skip3
         j skip1