package com.example.studyhard.calculator;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView txtFormula;
    private TextView txtValue;

    public ArrayList<String> arylst_Formula = new ArrayList();
    public Stack<String> stack_Operator = new Stack<String>();
    public Stack<BigDecimal> stack_Value = new Stack<BigDecimal>();

    public String sFormula = "";
    public String sValue = "";
    public Boolean bIsPoint = false;
    public Boolean bIsResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    private void findViews() {
        txtFormula = (TextView) findViewById(R.id.txt_formula);
        txtValue = (TextView) findViewById(R.id.txt_value);
//        Button bHelp = (Button) findViewById(R.id.b_help);
    }

    private static int getPriority(String s)
    {
        if ( s.equals("+") || s.equals("-") )
        {
            return 1;
        }
        else if ( s.equals("*") || s.equals("/") )
        {
            return 2;
        }
        return 0;
    }

    public void calculate()
    {
        Boolean bIsDevideByZero = false;
        String sTmpNum = "";
        String sCurOpr = "";

        arylst_Formula.clear();
        stack_Operator.clear();
        stack_Value.clear();

        //no devide 0
        /*
        String 可能要先把數字和運算子切開
        運算子在堆疊中只能優先序大的壓優先序小的，也就是當運算子在進入堆疊之前和堆疊頂端的運算子比較優先序。如果外面的優先序大，則PUSH。否則就一直做pop，直到遇見優先序較小的運算子或堆疊為空。 值得注意的是:左括號在堆疊中優先序最小，亦即，任何運算子都可以壓它。
        使用迴圈，取出中序式的字元，遇運算元直接輸出；堆疊運算子與左括號； 堆疊中運算子優先順序大於讀入的運算子優先順序的話，直接輸出堆疊中的運算子，再將讀入的運算子置入堆疊；遇右括號輸出堆疊中的運算子至左括號。

        運算時由 後序式的前方開始讀取，遇到運算元先存入堆疊，如果遇到運算子，則由堆疊中取出兩個運算元進行對應的運算，然後將結果存回堆疊，如果運算式讀取完 畢，那麼堆疊頂的值就是答案了，例如我們計算12+34+*這個運算式（也就是(1+2)*(3+4)）：
        * */
        for (int i = 0; i < sFormula.length(); ++i)
        {
            switch (sFormula.charAt(i))
            {
                case '+':
                case '-':
                case '*':
                case '/':
                    //1. add the complete value into arrayList
                    arylst_Formula.add(sTmpNum);
                    sTmpNum = "";

                    //2. infix to postfix
                    //    while stack_Operator is not empty
                    //      compare the priority of current-operator with the top of stack_Operator
                    //      pop all operators with higher priority
                    //    finally push current operator

                    sCurOpr = sFormula.charAt(i)+"";

                    while (!stack_Operator.empty())
                    {
                        if ( getPriority(stack_Operator.peek()) < getPriority(sCurOpr) )
                        {
                            break;
                        }
                        arylst_Formula.add( stack_Operator.pop() );
                    }
                    stack_Operator.push(sCurOpr);

                    break;

                default: //number and point
                    sTmpNum += sFormula.charAt(i);
                    break;
            }
        }

        //3. After process all char at sFormula
        // 3.1 add the last value into arrayList
        arylst_Formula.add(sTmpNum);
        sTmpNum = "";

        // 3.2 pop the remain operators in stack_Operator
        while (!stack_Operator.empty())
        {
            arylst_Formula.add( stack_Operator.pop() );
        }


        // for all string in arylst_Formula
        for(String sTmp : arylst_Formula)
        {
            if (sTmp.charAt(0) == '+' || sTmp.charAt(0) == '-' || sTmp.charAt(0) == '*' || sTmp.charAt(0) == '/')
            {
                BigDecimal bigD_NumB = stack_Value.pop();
                BigDecimal bigD_NumA = stack_Value.pop();

                switch (sTmp.charAt(0))
                {
                    case '+':
                        stack_Value.push(bigD_NumA.add(bigD_NumB));
                        break;
                    case '-':
                        stack_Value.push(bigD_NumA.subtract(bigD_NumB));
                        break;
                    case '*':
                        stack_Value.push(bigD_NumA.multiply(bigD_NumB));
                        break;
                    case '/':
                        if (bigD_NumB.equals(0))
                        {
                            bIsDevideByZero = true;

                            new AlertDialog.Builder(this)
                                    .setMessage("Can not devide by 0")
                                    .setTitle("Info")
                                    .setPositiveButton("OK", null)
                                    .show();
                            break;
                        }
                        stack_Value.push(bigD_NumA.divide(bigD_NumB));
                        break;
                    default:
                        new AlertDialog.Builder(this)
                            .setMessage("arylst_Formula[current] is : " + sTmp)
                            .setTitle("Info")
                            .setPositiveButton("OK", null)
                            .show();
                        break;
                }

                if (bIsDevideByZero)
                {
                    break;
                }
                else {;}
            }
            else //num
            {
                BigDecimal bigD_Num = new BigDecimal(sTmp);
                stack_Value.push(bigD_Num);
            }
        }

        //update sValue
        if (bIsDevideByZero)
        {
            sValue = "";
            bIsResult = false;
        }
        else
        {
            sValue = stack_Value.pop().toString();
            bIsResult = true;
        }
    }

    public void input(String sInput)
    {
        switch (sInput) {
            case "c":
                //reset all object
                sFormula = "";
                sValue = "";
                bIsPoint = false;
                bIsResult = false;

                break;
            case "=":
                //if sValue is null, we can't press equals
                //  or just complete a calculation and no new input, we can't press equals
                //else calculate formula
                // reset bIsPoint and sFormula, update sValue and bIsResult
                if ( sValue == "" || bIsResult == true ) { ; }
                else
                {
                    if (sValue.charAt(sValue.length()-1) == '.')
                    {
                        sFormula += "0"; //eg. 1.= -> 1.0, 22.= -> 22.0
                    }
                    else { ; }

                    calculate();
                    sFormula = "";
                    bIsPoint = false;
                    //no need to check if the current result is point,
                    // since the result would only be used once input +-*/ immediately
                }

                break;
            case "+":
            case "-":
            case "*":
            case "/":
                //if sValue is null, do nothing
                // else if we just come out the result in sValue
                // else if not a complete decimal, we can't press operators
                // else reset sValue and update sFormula
                if ( sValue == "" || bIsResult == true ) { ; }
//                else if (bIsResult == true)
//                {
//                    sFormula += sValue + sInput;
//                    sValue = "";
//                    bIsPoint = false;
//                    bIsResult = false;
//
//                }
                else
                {
                    if (sValue.charAt(sValue.length()-1) == '.')
                    {
                        sFormula += "0"; //eg. 1. -> 1.0, 22. -> 22.0
                    }
                    else { ; }

                    sFormula += sInput;
                    sValue = "";
                    bIsPoint = false;
                }

                break;
            case ".":
                //once input num, means we don't want to use the last result
                if (bIsResult == true)
                {
                    bIsResult = false;
                    sValue = "";
                }
                else { ; }

                //already have one point
                if (bIsPoint == false)
                {
                    if (sValue == "")
                    {
                        sValue = "0.";
                        sFormula += "0.";
                    }
                    else //sValue is not null
                    {
                        sValue += sInput;
                        sFormula += sInput;
                    }
                    bIsPoint = true;
                }
                else
                { ; }
                break;

            default: //0~9
                //once input num, means we don't want to use the last result
                if (bIsResult == true)
                {
                    bIsResult = false;
                    sValue = "";
                }
                else { ; }

                if (sValue.equals("0"))
                {
                    //if sValue still be 0, than give it a number
                    sValue = sInput;
                    //change the last char of sFormula to the current input
                    sFormula = sFormula.substring(0, sFormula.length()-1) + sInput;
                }
                else
                {
                    sValue += sInput;
                    sFormula += sInput;
                }

                break;

        }// end switch

        //refresh result on txt_result
        if (sFormula == "")
        {
            txtFormula.setText("0");
        }
        else
        {
            txtFormula.setText(sFormula);
        }
        if (sValue == "")
        {
            txtValue.setText("0");
        }
        else
        {
            txtValue.setText(sValue);
        }


        Log.d("CALCULATOR", "sFormula = " + sFormula);
        Log.d("CALCULATOR", "sValue = " + sValue);
    }

    //Operators
    public void onClick_imgBtn_plus(View V) {
        input("+");
    }

    public void onClick_imgBtn_minus(View V) {
        input("-");
    }

    public void onClick_imgBtn_times(View V) {
        input("*");
    }

    public void onClick_imgBtn_devide(View V) {
        input("/");
    }

    //Special button
    public void onClick_imgBtn_reset(View V) {
        input("c");
    }

    public void onClick_imgBtn_equals(View V) {
        input("=");
        ;
    }

    public void onClick_imgBtn_changeSign(View V) {
//        input("+");
        ;
    }

    public void onClick_imgBtn_percent(View V) {
//        input("+");
        ;
    }

    //Number button
    public void onClick_imgBtn_point(View V) {
        input(".");
    }

    public void onClick_imgBtn_0(View V) {
        input("0");
    }

    public void onClick_imgBtn_1(View V) {
        input("1");
    }

    public void onClick_imgBtn_2(View V) {
        input("2");
    }

    public void onClick_imgBtn_3(View V) {
        input("3");
    }

    public void onClick_imgBtn_4(View V) {
        input("4");
    }

    public void onClick_imgBtn_5(View V) {
        input("5");
    }

    public void onClick_imgBtn_6(View V) {
        input("6");
    }

    public void onClick_imgBtn_7(View V) {
        input("7");
    }

    public void onClick_imgBtn_8(View V) {
        input("8");
    }

    public void onClick_imgBtn_9(View V) {
        input("9");
    }
}
