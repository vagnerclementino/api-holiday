// Demo simples das classes Holiday
// Run: jshell --class-path target/classes demo.jsh

import java.time.*;
import java.util.*;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.domain.oop.*;

System.out.println("=== Demo Simples Holiday API ===\n");

List<Locality> localities = List.of();

// 1. ANO NOVO (FixedHoliday)
System.out.println("1. ANO NOVO (FixedHoliday)");
FixedHoliday anoNovo = new FixedHoliday("Ano Novo", "Primeiro dia do ano", 1, Month.JANUARY, localities, HolidayType.NATIONAL);
anoNovo.setDate(2025);
System.out.println("getDate(): " + anoNovo.getDate());
System.out.println("getObserved(): " + anoNovo.getObserved());
System.out.println("toString(): " + anoNovo.toString());
System.out.println();

// 2. PÁSCOA (MoveableHoliday - Lunar)
System.out.println("2. PÁSCOA (MoveableHoliday - Lunar)");
MoveableHoliday pascoa = new MoveableHoliday("Easter Sunday", "Domingo de Páscoa", localities, HolidayType.RELIGIOUS, MoveableHolidayType.LUNAR_BASED);
pascoa.setDate(2025);
System.out.println("getDate(): " + pascoa.getDate());
System.out.println("getObserved(): " + pascoa.getObserved());
System.out.println("toString(): " + pascoa.toString());
System.out.println();

// 3. SEXTA-FEIRA DA PAIXÃO (MoveableHoliday - Relativa à Páscoa)
System.out.println("3. SEXTA-FEIRA DA PAIXÃO (MoveableHoliday - Relativa)");
MoveableHoliday sextaPaixao = new MoveableHoliday("Sexta-feira da Paixão", "Sexta-feira Santa", localities, HolidayType.RELIGIOUS, pascoa, -2);
sextaPaixao.setDate(2025);
System.out.println("getDate(): " + sextaPaixao.getDate());
System.out.println("getObserved(): " + sextaPaixao.getObserved());
System.out.println("toString(): " + sextaPaixao.toString());
System.out.println();

// 4. NATAL COM MONDAYISATION (FixedHoliday - Sábado vira Segunda)
System.out.println("4. NATAL COM MONDAYISATION (FixedHoliday)");
FixedHoliday natal = new FixedHoliday("Natal", "Natal com mondayisation", 25, Month.DECEMBER, localities, HolidayType.RELIGIOUS, true);
natal.setDate(2021); // 2021: Natal caiu no sábado
System.out.println("getDate(): " + natal.getDate() + " (SÁBADO)");
System.out.println("getObserved(): " + natal.getObserved() + " (SEGUNDA)");
System.out.println("toString(): " + natal.toString());

System.out.println("\n=== Demo Completo ===");

/exit
