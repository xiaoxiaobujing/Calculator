package com.datastructure.stack;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    public static void main(String[] args) {
        String expression = "-100+(-1*6-(5*(1+1)+(6*1)))";
        // 创建一个栈，使用栈方法
        ArrayStack2 operStack = new ArrayStack2(10);
        // 定义需要的相关变量
        int index = 0;
        char ch = ' '; // 将每次扫描得到的char保存到ch
        // 开始while循环的扫描expression
        while (true) {
            // 依次得到expression 的每一个字符
            ch = expression.substring(index, index + 1).charAt(0);
            // 判断ch是什么，然后做相应的处理
            if (operStack.isBracketHead(ch)) { // 如果是括号符
                expression=handle(expression);
                index=0;
                continue;
            }
            index++;
            if (index >= expression.length()){
                break;
            }
        }
        getResult(expression);
    }

    public static String handle(String expression){
        char ch;
        int index = 0;
        int startBracket=-1;
        int endBracket=-1;
        // 创建一个栈，使用栈方法
        ArrayStack2 operStack = new ArrayStack2(10);
        while(true){
            // 依次得到expression 的每一个字符 得到一个小括号的位置
            ch = expression.substring(index, index + 1).charAt(0);
            if(operStack.isBracketHead(ch)){
                startBracket=index;
            } else if(operStack.isBracketTail(ch)) {
                endBracket=index;
                break;
            }
            index++;
        }
        if(startBracket>=0) {
            String mediate = expression.substring(startBracket + 1, endBracket);
            String result = String.valueOf(getResult(mediate));
            // 取代原位置
            expression = expression.replace("(" + mediate + ")", result);
        }
        return expression;
    }

    public static int getResult(String expression){
        // 创建两个栈，数栈，一个符号栈
        ArrayStack2 numStack = new ArrayStack2(10);
        ArrayStack2 operStack = new ArrayStack2(10);
        // 定义需要的相关变量
        int index = 0;
        int num1 = 0;
        int num2 = 0;
        int oper = 0;
        int res = 0; // 结果
        char ch = ' '; // 将每次扫描得到的char保存到ch
        String keepNum = ""; // 用于拼接多位数
        boolean isSubtract=false;
        // 开始while循环的扫描expression
        while (true){
            // 依次得到expression 的每一个字符
            ch = expression.substring(index,index+1).charAt(0);
            // 判断第一个字符是不是减号
            if(operStack.isSubtract(ch)&&index==0){
                isSubtract = true;
                index++;
                continue;
            }
            // 判断ch是什么，然后做相应的处理
            if (operStack.isOper(ch)){ // 如果是运算符
                // 判断运算符之后是不是剪号
                if(operStack.isSubtract(expression.substring(index+1,index+2).charAt(0))){
                    isSubtract = true;
                    index++;
                    continue;
                }
                // 判断当前的符号栈是否为空
                if(!operStack.isEmpty()){
                    // 如果符号栈有操作符，就进行比较，如果当前的操作符的优先级小于或者等于栈中的操作符
                    // 就需要从数栈中pop出两个数
                    // 再从符号栈中pop出一个字符，进行运算，将得到结果，入数栈，然后将当前的操作符入符号栈
                    if(operStack.priority(ch) <= operStack.priority(operStack.peek())){
                        num1 = numStack.pop();
                        num2 = numStack.pop();
                        oper = operStack.pop();
                        res = numStack.cal(num1,num2,oper);
                        // 把运算的结果加入数栈
                        numStack.push(res);
                        // 然后把当前的操作符加入符号栈
                        operStack.push(ch);
                    } else {
                        // 如果当前操作符的优先级大于栈中的操作符，就直接入符号栈
                        operStack.push(ch);
                    }
                } else {
                    // 如果当前的符号栈为空直接入符号栈
                    operStack.push(ch); // 1 + 3
                }
            } else {
                // 如果是数，则直接入数栈
                // numStack.push(ch - 48); // 1 + 3 对照ASC-II对照表
                // 1. 当处理多位数时，不能发现一个数就立即入栈，因为他可能是多位数
                // 2. 在处理数，需要向expression的表达式的index 后再看一位，如果是数就进行扫描，如果是符号才入栈
                // 3. 因此我们需要定义一个变量字符串，用于拼接

                // 处理多位数
                keepNum += ch;

                // 如果ch已经是expression的最后一位，就直接入栈
                if (index == expression.length() - 1){
                    numStack.push(Integer.parseInt(keepNum));
                } else {
                    // 判断下一个字符是不是数字，如果是数字，就继续扫描，如果是运算符，则入栈
                    // 注意是看后一位，不是则index++
                    if(operStack.isOper(expression.substring(index+1,index+2).charAt(0))){
                        // 如果后一位是运算符，则入栈keepNum = "1" 或者 "123"
                        // 如果是负数或者运算字符串的第一个字符为负号
                        if(isSubtract) {
                            numStack.push(Integer.parseInt("-"+keepNum));
                            isSubtract=false;
                        }else {
                            numStack.push(Integer.parseInt(keepNum));
                        }
                        // 重要的！！！！，keepNum清空
                        keepNum = "";
                    }
                }
            }
            // 让 index + 1,并判断是否扫描到expression最后
            index++;
            if(index >= expression.length()){
                break;
            }
        }

        // 当表达式扫描完毕，就按顺序从数栈和符号栈中pop出相应的数和符号，并运行
        while (true){
            // 如果符号栈为空，则计算到最后的结果，数栈中只有一个数字【结果】
            if(operStack.isEmpty()){
                break;
            }
            num1 = numStack.pop();
            num2 = numStack.pop();
            oper = operStack.pop();
            res = numStack.cal(num1,num2,oper);
            numStack.push(res); //入栈
        }
        // 将数栈中的最后数，pop出，就是结果
        int res2 = numStack.pop();
        System.out.printf("表达式%s = %d",expression,res2);
        return res2;
    }
}

// 先创建一个栈
// 定义一个ArrayStack表示栈
class ArrayStack2 {
    private int maxSize; // 栈的大小
    private int[] stack; // 数组，数组模拟栈，数据就放在该数组
    public int top = -1; // top表示栈顶，初始化值为-1

    // 构造器
    public ArrayStack2(int maxSize) {
        this.maxSize = maxSize;
        stack=new int[maxSize];
    }

    // 增加一个方法，可以返回当前栈顶的值，但是不是真正的pop
    public int peek() {
        return stack[top];
    }

    // 栈满
    public boolean isFull() {
        return top == maxSize - 1;
    }

    // 栈空
    public boolean isEmpty(){
        return top == -1;
    }

    // 入栈-push
    public void push(int value){
        // 先判断栈是否满
        if(isFull()){
            System.out.println("栈满");
            return;
        }
        top++;
        stack[top] = value;
    }

    // 出栈-pop,将栈顶的数据返回
    public int pop(){
        // 先判断栈是否为空
        if(isEmpty()){
            // 抛出异常
            throw new RuntimeException("栈空,没有数据～");
        }
        int value = stack[top];
        top--;
        return value;
    }

    // 显示栈的情况[遍历栈]，遍历时，需要从栈顶开始显示数据
    public void list(){
        if(isEmpty()){
            System.out.println("栈空，没有数据～～");
            return;
        }
        // 需要从栈顶开始显示数据
        for(int i = top; i >= 0; i--){
            System.out.printf("stack[%d]=%d\n",i,stack[i]);
        }
    }

    // 返回运算符的优先级，数字越大，则优先级越高
    public int priority(int oper){
        if(oper == '*' || oper == '/'){
            return 1;
        } else if (oper == '+' || oper == '-'){
            return 0;
        } else {
            return -1; // 假定目前的表达式只有+，-，*，/
        }
    }

    public boolean isBracket(char val){
        return val == '(' || val == ')';
    }

    public boolean isBracketHead(char val){
        return val == '(';
    }

    public boolean isBracketTail(char val){
        return val == ')';
    }

    public boolean isSubtract(char val){
        return val == '-';
    }
    // 判断是不是一个运算符
    public boolean isOper(char val){
        return val == '+' || val == '-' || val == '*' || val == '/';
    }

    // 计算方法
    public int cal(int num1,int num2,int oper){
        int res = 0; // res 用于存放计算的结果
        switch (oper) {
            case '+':
                res = num1 + num2;
                break;
            case '-':
                res = num2 - num1;// 注意顺序
                break;
            case '*':
                res = num1 * num2;
                break;
            case '/':
                res = num2 / num1;
                break;
            default:
                break;
        }
        return res;
    }
}

