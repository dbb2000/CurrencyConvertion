package br.com.cwi.convertion.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static org.junit.Assert.*;
import org.junit.Test;

import br.com.cwi.convertion.Convertion;
import br.com.cwi.convertion.exception.CurrencySymbolNotFoundException;
import br.com.cwi.convertion.exception.ValueLessThanZeroException;

public class ConvertionTest {

	
	@Test(expected=CurrencySymbolNotFoundException.class)
	public void testCurrencyQuotationTestIfFromParameterIsNotFound() throws IOException {
				
		Convertion conv = new Convertion();
		conv.currencyQuotation("XXX", "EUR", 100.00, "06/10/2016");
			
	}

	@Test(expected=CurrencySymbolNotFoundException.class)
	public void testCurrencyQuotationTestIfToParameterIsNotFound() throws IOException {
				
		Convertion conv = new Convertion();
		conv.currencyQuotation("USD", "XXX", 100.00, "06/10/2016");
			
	}
	
		
	@Test(expected=ValueLessThanZeroException.class)
	public void testValueParameterLessThanZero() throws IOException {
				
		Convertion conv = new Convertion();
		conv.currencyQuotation("USD", "EUR", -100.00, "06/10/2016");
			
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testIfSomeGivenDateFileNotFound() throws IOException {
				
		Convertion conv = new Convertion();
		conv.currencyQuotation("USD", "EUR", 100.00, "01/10/2016");
			
	}
	
	@Test
	public void testCurrencyConvertionGBPtoUSD() throws IOException {
	
		File file = new File("src/csv/" + "20160909.csv");
		this.writeCsvFile(file);
		
		Convertion conv = new Convertion();
		BigDecimal value = conv.currencyQuotation("GBP", "USD", 100.00, "09/09/2016");
		BigDecimal expected  = new BigDecimal("126.44");
		
		
		assertEquals(value, expected);
		
	}
	
	
	
	
	private void writeCsvFile(File file) throws IOException {		
		PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(
						new FileOutputStream(file),
						"utf-8"
				)
		);
		writer.println("09/09/2016;540;B;GBP;4,08480000;4,08620000;1,26450000;1,26470000");
		writer.println("09/09/2016;220;A;USD;3,23040000;3,23100000;1,00000000;1,00000000");

		writer.flush();
		writer.close();				
		if ( writer.checkError() ) fail("Error to write file");
	}
	
	
}
