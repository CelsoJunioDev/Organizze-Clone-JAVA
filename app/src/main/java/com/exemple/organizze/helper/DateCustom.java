package com.exemple.organizze.helper;

import com.exemple.organizze.model.Movimentacao;

import java.text.SimpleDateFormat;

public class DateCustom {
    public  static String dataAtual (){
       long data = System.currentTimeMillis(); //recupera a data atual
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy"); //configura o formato desejado
        String dataString = simpleDateFormat.format(data); //adiciona o formato
        return dataString;
    }
    public static String mesAnoDataEscolhida(String data){
        //EX: 11/08/2020
        String retornoData[] =  data.split("/"); //SPLIT quebra uma string e varias partes de acordo com o caracter digitado
        String dia = retornoData [0]; //11
        String mes = retornoData [1]; //08
        String ano = retornoData [2]; // 2020
        String mesAno = mes + ano;
        return mesAno;

    }
}
