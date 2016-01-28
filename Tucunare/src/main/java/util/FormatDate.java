package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatDate {

	public static String dataLimit(String pullRequestDateString, int days){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date pullRequestDate = null;
		String beforeDate="";
		try {
			pullRequestDate = formatter.parse(pullRequestDateString);
			GregorianCalendar pastDate = new GregorianCalendar();
			pastDate.setTime(pullRequestDate);
			days = days * -1;
			pastDate.add(Calendar.DAY_OF_YEAR, days);
			Date d = pastDate.getTime();
			beforeDate = formatter.format(d);
		} catch (ParseException e){
			formatter = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
			try {
				pullRequestDate = formatter.parse(pullRequestDateString);
				GregorianCalendar pastDate = new GregorianCalendar();
				pastDate.setTime(pullRequestDate);
				days = days * -1;
				pastDate.add(Calendar.DAY_OF_YEAR, days);
				Date d = pastDate.getTime();
				beforeDate = formatter.format(d);
			} catch (ParseException e1) {
				System.err.println("Erro ao executar o método FormatDate.dataLimit() data: "+pullRequestDateString);
			}

		}
		return beforeDate;	
	}

	public static String dataLimitMonth(String pullRequestDateString, Integer months){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date pullRequestDate = null;
		String beforeDate="";
		months = months * -1;
		try {
			pullRequestDate = formatter.parse(pullRequestDateString);
			GregorianCalendar pastDate = new GregorianCalendar();
			pastDate.setTime(pullRequestDate);
			pastDate.add(Calendar.MONTH, months);
			Date d = pastDate.getTime();
			beforeDate = formatter.format(d);
		} catch (ParseException e){
			System.err.println("Erro na conversão de data do autor do commit.");
		}
		return beforeDate;	
	}

	public static String getLifetime(String closedData, String createdData){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date cloDate, creDate; 
		long diferencaMinutos= 0L;
		try {
			cloDate = formatter.parse(closedData);
			Calendar clocDate = Calendar.getInstance();
			clocDate.setTime(cloDate);

			creDate = formatter.parse(createdData);
			Calendar crecDate = Calendar.getInstance();
			crecDate.setTime(creDate);

			long lifetime = clocDate.getTimeInMillis() - crecDate.getTimeInMillis(); 
			int tempoMinutos = 1000*60;
			diferencaMinutos = lifetime/tempoMinutos;
		} catch (ParseException e){
			SimpleDateFormat formatter2 = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
			try {
				cloDate = formatter2.parse(closedData);
				Calendar clocDate = Calendar.getInstance();
				clocDate.setTime(cloDate);

				creDate = formatter.parse(createdData);
				Calendar crecDate = Calendar.getInstance();
				crecDate.setTime(creDate);

				long lifetime = clocDate.getTimeInMillis() - crecDate.getTimeInMillis(); 
				int tempoMinutos = 1000*60;
				diferencaMinutos = lifetime/tempoMinutos;
			} catch (ParseException e1) {
				try{
					cloDate = formatter.parse(closedData);
					Calendar clocDate = Calendar.getInstance();
					clocDate.setTime(cloDate);

					creDate = formatter2.parse(createdData);
					Calendar crecDate = Calendar.getInstance();
					crecDate.setTime(creDate);

					long lifetime = clocDate.getTimeInMillis() - crecDate.getTimeInMillis(); 
					int tempoMinutos = 1000*60;
					diferencaMinutos = lifetime/tempoMinutos;
				}catch(ParseException pe){
					System.err.println("Erro executar o método FormatDate.getLifeTime");
				}
			}			
		}catch (Exception e){
			System.err.println("Erro geral de data do autor do commit.");
		}
		return ""+diferencaMinutos;	
	}

	public static String getAge(String data){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		Date date;
		long diferenceDays = 0L;
		try {
			date = formatter.parse(data);
			Calendar dateCalendar = Calendar.getInstance();
			dateCalendar.setTime(date);

			Calendar currentDate = Calendar.getInstance();
			currentDate.getTime();

			long lifetime = currentDate.getTimeInMillis() - dateCalendar.getTimeInMillis();
			int timeDay = 1000*60*60*24;
			diferenceDays = lifetime/timeDay;
		} catch (ParseException e){
			System.err.println("Erro na conversão de data do autor do commit.");
		}
		return ""+diferenceDays;	
	}

	public static String getDate(String data){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date;
		Calendar dateCalendar = null;
		String d="";
		try {
			date = formatter.parse(data);
			dateCalendar = Calendar.getInstance();
			dateCalendar.setTime(date);
			d = formatter2.format(date);
		} catch (ParseException e){
			System.err.println("Erro na conversão de data do autor do commit.");
		}
		return d;	
	}
}