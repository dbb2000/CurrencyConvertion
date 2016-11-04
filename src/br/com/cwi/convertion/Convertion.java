package br.com.cwi.convertion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.cwi.convertion.exception.CurrencySymbolNotFoundException;
import br.com.cwi.convertion.exception.ValueLessThanZeroException;

public class Convertion {
	
	/**
	 * @param from = String with the currency name (example "USD") you want to convert.
	 * @param to = String with the currency name (example "EUR") you want to see the result.
	 * @param value = The value that should be converted. The currency of this value will be expressed in the “from” parameter.
	 * @param quotation = A date as String in the format “dd/mm/yyyy”
	 * @return
	 * @throws IOException 
	 */
	public BigDecimal currencyQuotation(String from, String to, Number value, String quotation) throws IOException{
		
		//negative value verification 
		if(value.doubleValue() < 0){
			throw new ValueLessThanZeroException("Negative values are not allowed");			
		}
		
		String fileName = getFileName(quotation);
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader("src/csv/" + fileName ));
		} catch (IOException e) {
			throw new FileNotFoundException("CSV file not found with given date:" + quotation);
		}

		String currentLine;
		String[] fromLine = null;
		String[] toLine = null;
		
		/**
		 * CSV fields description:
		 * [0] = Data
		 * [1] = Cod. Moeda
		 * [2] = Tipo
		 * [3] = Moeda
		 * [4] = Taxa Compra
		 * [5] = Taxa Venda
		 * [6] = Paridade Compra
		 * [7] = Paridade Venda
		 */				
		
		while ((currentLine = br.readLine()) != null) {
		    
		    String[] row = currentLine.split(";");
		    
		    if(row[3].equals(from)) fromLine = row;
		    if(row[3].equals(to)) toLine = row;		    
		}
		br.close();
		
		if(fromLine == null) throw new CurrencySymbolNotFoundException("Currency symbol not found: " + from);
		if(toLine == null) throw new CurrencySymbolNotFoundException("Currency symbol not found: " + to);
				
		BigDecimal fromValueTxCompra = new BigDecimal(fromLine[4].replaceAll(",", "."));
		BigDecimal tempValue = new BigDecimal(value.toString().replaceAll(",", "."));
		BigDecimal toValueTxCompra = new BigDecimal(toLine[4].replaceAll(",", "."));
		
		BigDecimal baseConvertion = fromValueTxCompra.divide(toValueTxCompra, MathContext.DECIMAL128);
		
		BigDecimal totalConvertedAmount = baseConvertion.multiply(tempValue)
														.setScale(2, RoundingMode.DOWN);
		
		printResult(totalConvertedAmount, fromLine, toLine, value);
		
		return totalConvertedAmount;
		
	}


	private String getFileName(String quotation) {
		
		SimpleDateFormat  formatter = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();
		
		try {
			Date date = formatter.parse(quotation);			
			calendar.setTime(date);
			
		} catch (ParseException e) {
			System.err.println("Invalid quotation argument: " + e);
			System.exit(-1);
		} 
		
		if( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			calendar.add(Calendar.DATE, 2);
		}
		
		if( calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			calendar.add(Calendar.DATE, 1);
		}
		
		SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
		String newDate = s.format(calendar.getTime());
				
		String[] temp = newDate.split("/");
		String label = temp[2] + temp[1] + temp[0] + ".csv";
				
		return label;
	}

	private void printResult(BigDecimal totalConvertedAmount, String[] fromLine, String[] toLine, Number value) {
		
		String convertedCommaValue = totalConvertedAmount.toString();
		
		System.out.println("Converting " + fromLine[3] + " " + value + " to " + toLine[3] + " = " + convertedCommaValue );	
		System.out.println("Data source: ");
		System.out.println( fromLine[0] + " - " + fromLine[1] + " - " + fromLine[2] + " - " + fromLine[3] + " - " + fromLine[4]);
		System.out.println( toLine[0] + " - " + toLine[1] + " - " + toLine[2] + " - " + toLine[3] + " - " + toLine[4]);		
		
	};
}
