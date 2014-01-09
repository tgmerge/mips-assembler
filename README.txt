MIPS-assembler
==============

一个java实现的MIPS汇编器。

2013秋冬《汇编与接口》课程作业的某部分

by tgmerge

## 使用方法

要汇编test.asm的话，请在命令行中使用以下命令

	java -jar Assembler.jar test.asm

参见Assembler.bat的内容。

## TODO 未完成的特性

 * 支持.伪指令
 * 完善输出形式(根据参数选择到toBinFile、toBinText、toHexText)
 * (?)支持16进制、2进制直接数

## 约定

基本格式：

 * 每条指令占一行，缩进、空白任意
 * 标号单独占一行
 * 数据全部以十进制表示
 * 单行注释以#开始。

尚未支持的特性：

 * 程序分段
 * 含.的伪指令

支持的特性：

 * 所有MIPS基本指令和伪指令
 * 标号跳转
 * 增加一条指令direct imm32，用于实现.伪指令

## 错误处理

汇编器可以识别基本的代码错误。包括：

 * 错误的指令  
   ```addx $t0, $t1, $t2```
 * 错误的参数数量  
   ```j target, $zero```
 * 错误的参数类型  
   ```j $zero```
 * 无法识别的直接数  
   ```addi $t0, $t1, xxx```
 * 无法识别的标号  
   ```j undefinedLabel```
 * 重复定义的标号  

等，并给出相应位置和提示。

## 流程

 1. 文本预处理  
    Preprocesser类。根据字典pre.txt正则表达式替换多余的符号，包括多余的空白符、换行、符号前后空格等。
 2. 伪指令处理  
    PseudoReplacer类。根据字典pseudo.txt正则表达式展开伪指令。例如ble -> slt, beq
 3. 符号表扫描  
    SymbolScanner类。扫描并存储符号表。
 4. 指令翻译  
    OpTranslator类。根据字典op.txt(识别$x, imm, offset, target)翻译为最终的二进制编码。