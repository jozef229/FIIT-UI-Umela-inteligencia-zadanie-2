import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class zadanie_2a {
	//nastavene globalne premenne a konstanty
	private static final int ROW = 6, COLUMN = 6, DO_HLBKY = 1, DO_SIRKY = 0;
	private static final char HORIZONTAL = '1', VERTICAL = '0';
	private static int pocet_aut, najdene = 0, pocet_final_hlbka = 0, pocet_final_sirka = 0, pocet_final = -2;
	private static long zaciatok_cas = 0, rozdiel_cas = 0;
	private static Map<String, String> hash = new HashMap<String, String>();
	private static Queue<String> queue = new LinkedList<String>();
	private static Stack<String> stack = new Stack<String>();
	private static Scanner vstup;
	private static PrintWriter subor;
	
	//funkcia na zmenu casti stringu - ak chceme zmenit jedno pismeno
	public static String zmena_stringu(String string, int pos, char c) {
	    return string.substring(0, pos) + c + string.substring(pos + 1);
	  }
	
	//sluzi na vypis mapy do suboru a vypis cesty do terminalu
	private static void vypis(String map ,String pred) {
		pocet_final++;
		if(pred != "0") {
			subor.println(pocet_final);
			System.out.print(pocet_final + ": ");
		}
		//vypisuje kam sa pohlo ktore auticko do terminalu
		if(pred != "0"){
			for(int i = 0; i<pocet_aut; i++) {
				if(map.charAt(i*4) != pred.charAt(i*4)) {
					if((pred.charAt(i*4) - '0') < (map.charAt(i*4) - '0')) {
						subor.println("VPRAVO " + (char)(i+ 65) + "\n");
						System.out.println("VPRAVO " + (char)(i+ 65));
					}
					else {
						subor.println("VLAVO " + (char)(i+ 65) + "\n");
						System.out.println("VLAVO " + (char)(i+ 65));
					}
				}
				if(map.charAt(i*4+1) != pred.charAt(i*4+1)) {
					if((pred.charAt(i*4+1) - '0') < (map.charAt(i*4+1) - '0')) {
						subor.println("DOLE " + (char)(i+ 65) + "\n");
						System.out.println("DOLE " + (char)(i+ 65));
					}
					else {
						subor.println("HORE " + (char)(i+ 65) + "\n");
						System.out.println("HORE " + (char)(i+ 65));
					}
				}
			}
		}
		//vypisuje mapu ktora je zaznamenana v súbore
		char[][] pole = new char[ROW][COLUMN];
		for(int i= 0;i<ROW;i++ ) {
			for(int j = 0;j<COLUMN;j++) pole[i][j] = '-';
		}
		for(int i =0;i<pocet_aut;i++) {
			for(int j = 0; j<(map.charAt(i*4+2) -'0');j++){
				if(map.charAt(i*4+3) == VERTICAL)pole[(map.charAt(i*4+1) -'0')+j][(map.charAt(i*4) -'0')] = (char)(i+ 65);
				if(map.charAt(i*4+3) == HORIZONTAL)pole[(map.charAt(i*4+1) -'0')][(map.charAt(i*4) -'0')+j] = (char)(i+ 65);
			}
		}
		for(int i= 0;i<ROW;i++ ) {
			for(int j = 0;j<COLUMN;j++)subor.print(pole[i][j]);
			subor.println();
		}
		subor.println("---------");
	}
	
	//rekurzivna funkcia pre vypis mapy (vola funkciu vypisu a vybera stavy z hash tabulky)
	private static void vypis_vysledok(String aktualna, int druh_prehladavania) {
		String predchadzajuca = hash.get(aktualna);
		if(predchadzajuca != null) {
			vypis_vysledok(predchadzajuca, druh_prehladavania);
			vypis(aktualna, predchadzajuca);
		}
	}
	
	//funkcia ktora porovnava ci sa dane auto sme pohnut voci hracej ploche (ci auto "nevypadne")
	//taktiez kontroluje ci sa dany stav ktory chceme zapisat do hash tabulky v nej uz nenachádza.
	//ak sa nenachadza zapise ho do hash tabulky a stacku/queue
	//ako posledne kontroluje ci dany stav nie je vysldny a ak je ukonci prehladavanie a spusti funkciu na vypis
	private static void vloz_hash(String last_map, int smer, int i, int druh_prehladavania) {
		String mapa = last_map; 
		switch(smer) {
			case 1:{ //vlavo
				if((mapa.charAt(i*4) -'0' - 1) < 0) smer = 0;
				else mapa = zmena_stringu(mapa, (i*4), (char)((mapa.charAt(i*4) -'0' - 1) + '0'));
			}break;
			case 2:{ //vpravo
				if((mapa.charAt(i*4) -'0' + ((mapa.charAt(i*4+2) -'0')) + 1) > COLUMN) smer = 0;
				else mapa = zmena_stringu(mapa, (i*4), (char)((mapa.charAt(i*4) -'0' + 1) + '0'));
			}break;
			case 3:{ //hore
				if((mapa.charAt(i*4+1) -'0' - 1) < 0) smer = 0;
				else mapa = zmena_stringu(mapa, (i*4+1), (char)((mapa.charAt(i*4+1) -'0' - 1) + '0'));
			}break;
			case 4:{ //dole
				if((mapa.charAt(i*4+1) -'0' + ((mapa.charAt(i*4+2) -'0')) + 1) > ROW) smer = 0;
				else mapa = zmena_stringu(mapa, (i*4+1), (char)((mapa.charAt(i*4+1) -'0' + 1) + '0'));
			}break;
			default: ;break;
		}
		if(smer != 0) {
			if(!hash.containsKey(mapa)) {
				hash.put( mapa, last_map);
				if(druh_prehladavania == 0)queue.add(mapa);
				else stack.push(mapa);
			}
			if((mapa.charAt(0) -'0') == (COLUMN - 2)) {
				rozdiel_cas = System.currentTimeMillis() - zaciatok_cas;
				najdene = 1;
				vypis_vysledok(mapa, druh_prehladavania);
			}
		}
	}
	
	//funkcia kontroluje ci sa auto moze posunut do jednotlivych stran ale iba voci ostatnym autam a nakoniec zavola funkciu vloz_hash
	public static void kontrola_posunu(String mapa, int i, int poloha, int druh_prehladavania) {
		int suradnica1 = 3 - (2*poloha);
		int suradnica2 = 4 - (2*poloha);
		for(int j = 0;j<pocet_aut;j++) {
			if(i != j && (mapa.charAt(j*4+3) - '0') == poloha && mapa.charAt(i*4 + poloha) == mapa.charAt(j*4 + poloha)) {
				if(((mapa.charAt(j*4+(1-poloha)) -'0') + (mapa.charAt(j*4+2) -'0')) == (mapa.charAt(i*4+(1-poloha)) -'0')) suradnica1 = 0;
				if((mapa.charAt(i*4+(1-poloha)) -'0') + (mapa.charAt(i*4+2) -'0') == (mapa.charAt(j*4+(1-poloha)) -'0')) suradnica2 = 0;
			}
			if(i != j && (mapa.charAt(j*4+3) - '0') == (1-poloha)) {
				if(((mapa.charAt(i*4 + poloha) -'0') >= (mapa.charAt(j*4 + poloha) -'0')) && ((mapa.charAt(i*4 + poloha) -'0') < (mapa.charAt(j*4 + poloha) -'0') + (mapa.charAt(j*4+2) -'0'))) {
					if((mapa.charAt(i*4+(1-poloha)) -'0' + (mapa.charAt(i*4+2) -'0')) == (mapa.charAt(j*4+(1-poloha)) -'0'))suradnica2 = 0;
					if((mapa.charAt(i*4+(1-poloha)) -'0' - 1) == (mapa.charAt(j*4+(1-poloha)) -'0'))suradnica1 = 0;
				}
			}
		}
		vloz_hash(mapa, suradnica1, i, druh_prehladavania);
		if(najdene != 1)vloz_hash(mapa, suradnica2, i, druh_prehladavania);
	}
	
	//funkcia vyberie z radu/zasobnika stav prejde kazde auto stavu a zavola funkciu kontrola_posunu
	public static void prehladavanie(String car, int druh_prehladavania) {
		pocet_final = 0;
		najdene = 0;
		hash.clear();
		hash.put(car,null);
		if(druh_prehladavania == DO_SIRKY)queue.add(car);
		else stack.push(car);
		zaciatok_cas = System.currentTimeMillis();;
		while((druh_prehladavania == DO_SIRKY ? !queue.isEmpty() : !stack.isEmpty()) && najdene == 0) {
			String mapa;
			if(druh_prehladavania == DO_SIRKY)mapa = queue.remove();
			else mapa = stack.pop();
			for(int i=0; i<pocet_aut; i++)kontrola_posunu(mapa,i,mapa.charAt(i*4+3) - '0', druh_prehladavania);
		}
	}
	
	//hlavna funkcia ktorá slúzi na spustenie prehladavania a spracovanie vstupu
	//taktiez riesi vypis zakladnych veci
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		subor = new PrintWriter("blazniva_krizovatka.txt", "UTF-8");
		int pocet_hash_hlbka = 0, pocet_hash_sirka = 0;
		long sirka_cas, hlbka_cas;
		String car = "";
		vstup = new Scanner(System.in);
		System.out.println("Pomocka:\n\tX = x-ova suradnica\n\tY = y-ova suradnica\n\tD = dlzka (2/3)\n\tO = orientacia (0 - vertikalne/ 1 - horizontalne)\nAuticka budu pridelene pismena (1. auticko = A, 2. auticko B, ...)\nAuticka zadavajte v tvare 'XYDO' enter\nAko prve pri zadavani auticok zadajte unikove vozidlo.\n\nZadajte pocet aut:");
		pocet_aut = vstup.nextInt(); 
		if(pocet_aut == 0) {
			System.out.println("Chyba zadania");
			subor.close();
			System.exit(0);
		}
		System.out.println("Zadajte auticka:");
		for(int i = 0; i < pocet_aut; i++) car = car + vstup.next();
		subor.println("Vasa pociatocna pozicia vyzera takto:\n");
		vypis(car, "0");
		if((car.charAt(0) -'0') != (COLUMN - 2)) {
			System.out.println("\nPrehladavanie do Sirky:");
			subor.println("\nPrehladavanie do Sirky:");
			prehladavanie(car, DO_SIRKY);
			pocet_final_sirka = pocet_final;
			if(pocet_final_sirka == 0 ) {
				rozdiel_cas = System.currentTimeMillis() - zaciatok_cas;
				System.out.println("Neexistuje riesenie");
				subor.println("Neexistuje riesenie");
			}
			sirka_cas = rozdiel_cas;
			pocet_hash_sirka = hash.size();
			System.out.println("\nPrehladavanie do Hlbky:");
			subor.println("\nPrehladavanie do Hlbky:");
			prehladavanie(car, DO_HLBKY);
			pocet_final_hlbka = pocet_final;
			if(pocet_final_hlbka == 0 ) {
				rozdiel_cas = System.currentTimeMillis() - zaciatok_cas;
				System.out.println("Neexistuje riesenie");
				subor.println("Neexistuje riesenie");
			}
			hlbka_cas = rozdiel_cas;
			pocet_hash_hlbka = hash.size();
			System.out.println("Pocet krokov do cielovej pozicie:\n\tdo sirky: " + pocet_final_sirka + "\n\tdo hlbky: " + pocet_final_hlbka );
			System.out.println("Pocet prejdenych stavov:\n\tdo sirky: " + pocet_hash_sirka + "\n\tdo hlbky: " + pocet_hash_hlbka);
			System.out.println("Cas trvania prehladavania (bez vypisov):\n\tdo sirky: " + sirka_cas + "ms\n\tdo hlbky: " + hlbka_cas + "ms");
		}
		else System.out.println("Bol zadany stav ktory uz je riesenim!!!");
		subor.close();
	}
}