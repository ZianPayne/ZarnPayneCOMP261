

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class BoyerMooreTest {
	
	static String randomString(int len) {
		StringBuilder sb = new StringBuilder();
		
		for (int j = 0; j < len; j++) sb.append("abcdefghijklmnopqrstuvwxyz".charAt((int)(Math.random() * 26)));
		
		return sb.toString();
	}
	
	void  massBoyerMooreTester(int testNum, int maxTextLen, int maxRandPatternLen, int maxSubstringPatternLen) {
		for (int i = 0; i < testNum; i++) {
			if (i % (testNum / 100) == 0) System.out.println((i / (testNum / 100)) + "%");
			
			String text = randomString((int)(Math.random() * maxTextLen) + 0);
			
			String pattern;
			if (Math.random() > 0.5) pattern = randomString((int)(Math.random() * 20) + 0);
			else {
				int len = (int)(Math.random() * Math.min(maxSubstringPatternLen, text.length())) + 0;
				int index = (int)(Math.random() * (text.length() - len + 0));
				pattern = text.substring(index, index + len);
			}
			
			int expected = text.indexOf(pattern);
			int actual = BoyerMoore.search(pattern, text);
			if (actual != expected) {
				System.out.println("pattern: " + pattern);
				System.out.println("text: " + text);
				System.out.println("expected: " + expected);
				System.out.println("actual: " + actual);
				assert false;
			}
		}
	}
	
	/*

	@Test
	void  testBoyerMoore_1() {
		assert 784 == BoyerMoore.search("aviavia",
				"ziieihhslwxnzyyqduvtrjpykoeyebinmfpwhlrwqsqrybmwlwrmnlgrpnhvioagholsskkiclnonccldooztxiiyiqifsbyclnimtptpydtotyxzilhceubwimdytvhzdcqngnnbalffeqyckvxqtlhkjluwwaaiwdrndckmwwqrrwptseuqjvqzfudiekhowmyxkvnyuqgjqkalhbstrzuvmaecuxflyurdmxmvftqexnwdphujzxzdrtirbzwuadlehvykupgbeboelpdggdjalonxkhjtbhfggufrxioxqnnvzacjogdbjlzdyhfgfanzwpdijfznfsnmymkelfevmkbhvnxvcrqlgblakktzohncbhqwvlvbsefgkzxlpdmvddaavregqxsscifabjamxfmtxnsytxkcastxmwczlbtioifndgknmexdjvefsjlcplgqxapncedxddxskeismjqoslggjqmslxhwlkbhuneyhsysckazgptryetvughnwbbkgcgkqauwtxlbrshhecwybpoafgchfvzdxlvkerxzvxxsykjpxckzrxzkhsyidbnyzgdiiqvololaqduanbvkebicfgxlkqmaescdvmcfwfkffizhgskodcvgpxbquullhdldpplkhqczzoohebrbzquyhjauczgzxgizjxcmxkwihmgdnrdsrxxzlcirunibwnsmwudofekxctadpdyvxozebexfqzndnzrgzkkzppcbvigmwbcqphpvlcmbljrdobmbeamaviavialrhzsvgfoogxrelxiiusaqgwvolcynhfymxsstzvyboncbqyrlszrwlzkgsklbrybgqyomqbzphhvwlaqrlqcdolixhbqfpcgciylfcntevcyyqimoyboqjwzbzixiicwvftpavgkowfnqoihpmkkppddxtfugplwpijvwxko");
	}

	@Test
	void  testBoyerMoore_2() {
		assert 645 == BoyerMoore.search("wmnjbvdww",
				"cjbidpiqmxyxaesqhenxjbjujkbgbxznllxbszdhudwrmrxomrkquubzybgfwhtyxfpbwgmkyprmzvbnxeixvdoanemfyuquuureorsgegwjdfoacixsijyyjlaeywxhciyuoitrpaudsvaiepmllodobjpflkhfijrehmubfywomkchscolkhuwkhrnexyoisagwuecujzoiahlsilwuwhpuybaakqythumlmsimmjssvlijduwzneerzvzwgezpjdevvcjedmbfdfykhxuvfsufwkubhyvxyazdwnvmugfmssueuxxpkrugcyfnhxkuoklbmwewttnhjuptykefdliqoyvatthygdfdnsfyvqqaobzkgdjrllgqcdrspdkwrsaghwizzljhulvewfiswpcqmnavmpnwoqhmkdkaipqtofbwdgvtngreoxrsgficmoxeqcssmbsebrufhbzgzendqjlblxmrujajfpitczzdcpsgvjvwtzvibcoomuektrkeizjqmsjgwwmynxhkvsqyerujntvmfkwuwxcfuerjmyqhgtxqsqcxkkmkfmwqyxyjhfnbdbtmjepepvuogheuusbnbncfyvznaiatfxaifydceqmufaomkipwptbwkyewwmnjbvdwwgtqtpogwfrtkfvxjxqjklhsrqqlbzjesiomxybgsnckeaeflogpgecflgcbbxmqgyimxrhmurhlpnfuekruxpountreffohukydtbbpwzhkwvjqyiwdrkxnbqbgluonpgsikwavdtzjedvkkjipezfdsoecoypvdxkcoqdcbhlrbiopzmgvordwuaqypszcrxduheoylyngrnwnlrqpwlrahtnugaxwywvsfkwgxjudkxbcvmrhyysklzjdqeebjitnxmgsifodxzfltmpqcpeeolkpjohthggihycxwvjvbtmzpk");
	}

	@Test
	void  testBoyerMoore_3() {
		assert 6 == BoyerMoore.search("jzgjj",
				"crmceujzgjjnyghadh");
	}
	*/

	
	@Test
	void  massTestBoyerMoore() {
		massBoyerMooreTester(1_000_000, 1_000, 20, 20);
		massBoyerMooreTester(10_000, 100_000, 20, 20);
		massBoyerMooreTester(1_000_000, 90, 100, 100);
	}
}

