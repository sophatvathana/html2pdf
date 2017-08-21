package com.github.sophatvathana.html2pdf.util;

import com.itextpdf.text.pdf.BidiLine;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.languages.LanguageProcessor;

import java.util.LinkedHashMap;
import java.util.Map;

public class KhmerLigaturizer implements LanguageProcessor{
	private final int _xx = 0;
	private final int CC_COENG = 7; // Subscript consonant combining character
	private final int CC_CONSONANT = 1; // Consonant of type 1 or independent vowel
	private final int CC_CONSONANT_SHIFTER = 5;
	private final int CC_CONSONANT2 = 2; // Consonant of type 2
	private final int CC_CONSONANT3 = 3; // Consonant of type 3
	private final int CC_DEPENDENT_VOWEL = 8;
	private final int CC_ROBAT = 6; // Khmer special diacritic accent -treated differently in state table
	private final int CC_SIGN_ABOVE = 9;
	private final int CC_SIGN_AFTER = 10;
	private final int CF_ABOVE_VOWEL = 536870912; // flag to speed up comparing
	private final int CF_CLASS_MASK = 65535;
	private final int CF_COENG = 134217728; // flag to speed up comparing
	private final int CF_CONSONANT = 16777216; // flag to speed up comparing
	private final int CF_DOTTED_CIRCLE = 67108864;
	
	// add a dotted circle if a character with this flag is the first in a syllable
	private final int CF_POS_ABOVE = 131072;
	private final int CF_POS_AFTER = 65536;
	private final int CF_POS_BEFORE = 524288;
	private final int CF_POS_BELOW = 262144;
	private final int CF_SHIFTER = 268435456; // flag to speed up comparing
	private final int CF_SPLIT_VOWEL = 33554432;
	private final int _c1 = CC_CONSONANT + CF_CONSONANT;
	private final int _c2 = CC_CONSONANT2 + CF_CONSONANT;
	private final int _c3 = CC_CONSONANT3 + CF_CONSONANT;
	private final int _co = CC_COENG + CF_COENG + CF_DOTTED_CIRCLE;
	private final int _cs = CC_CONSONANT_SHIFTER + CF_DOTTED_CIRCLE + CF_SHIFTER;
	private final int _da = CC_DEPENDENT_VOWEL + CF_POS_ABOVE + CF_DOTTED_CIRCLE + CF_ABOVE_VOWEL;
	private final int _db = CC_DEPENDENT_VOWEL + CF_POS_BELOW + CF_DOTTED_CIRCLE;
	private final int _dl = CC_DEPENDENT_VOWEL + CF_POS_BEFORE + CF_DOTTED_CIRCLE;
	private final int _dr = CC_DEPENDENT_VOWEL + CF_POS_AFTER + CF_DOTTED_CIRCLE;
	private final int _rb = CC_ROBAT + CF_POS_ABOVE + CF_DOTTED_CIRCLE;
	private final int _sa = CC_SIGN_ABOVE + CF_DOTTED_CIRCLE + CF_POS_ABOVE;
	private final int _sp = CC_SIGN_AFTER + CF_DOTTED_CIRCLE + CF_POS_AFTER;
	private final int _va = _da + CF_SPLIT_VOWEL;
	private final int _vr = _dr + CF_SPLIT_VOWEL;
	// flag for a split vowel -> the first part is added in front of the syllable
	private char BA;
	private char COENG;
	private String CONYO;
	private String CORO;
	
	private int[] khmerCharClasses = new int[] {
			_c1, _c1, _c1, _c3, _c1, _c1, _c1, _c1, _c3, _c1, _c1, _c1, _c1, _c3, _c1, _c1, _c1, _c1, _c1, _c1, _c3, _c1, _c1, _c1, _c1, _c3, _c2, _c1, _c1, _c1, _c3, _c3, _c1, _c3, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _c1, _dr, _dr, _dr, _da, _da, _da, _da, _db, _db, _db, _va, _vr, _vr, _dl, _dl, _dl, _vr, _vr, _sa, _sp, _sp, _cs, _cs, _sa, _rb, _sa, _sa, _sa, _sa, _sa, _co, _sa, _xx, _xx, _xx, _xx, _xx, _xx, _xx, _xx, _xx, _sa, _xx, _xx
	};
	private short[][] khmerStateTable = new short[][] {
			{
					1, 2, 2, 2, 1, 1, 1, 6, 1, 1, 1, 2
			}, {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
	}, {
			-1, -1, -1, -1, 3, 4, 5, 6, 16, 17, 1, -1
	}, {
			-1, -1, -1, -1, -1, 4, -1, -1, 16, -1, -1, -1
	}, {
			-1, -1, -1, -1, 15, -1, -1, 6, 16, 17, 1, 14
	}, {
			-1, -1, -1, -1, -1, -1, -1, -1, 20, -1, 1, -1
	}, {
			-1, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, -1
	}, {
			-1, -1, -1, -1, 12, 13, -1, 10, 16, 17, 1, 14
	}, {
			-1, -1, -1, -1, 12, 13, -1, -1, 16, 17, 1, 14
	}, {
			-1, -1, -1, -1, 12, 13, -1, 10, 16, 17, 1, 14
	}, {
			-1, 11, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1
	}, {
			-1, -1, -1, -1, 15, -1, -1, -1, 16, 17, 1, 14
	}, {
			-1, -1, -1, -1, -1, 13, -1, -1, 16, -1, -1, -1
	}, {
			-1, -1, -1, -1, 15, -1, -1, -1, 16, 17, 1, 14
	}, {
			-1, -1, -1, -1, -1, -1, -1, -1, 16, -1, -1, -1
	}, {
			-1, -1, -1, -1, -1, -1, -1, -1, 16, -1, -1, -1
	}, {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, 17, 1, 18
	}, {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 18
	}, {
			-1, -1, -1, -1, -1, -1, -1, 19, -1, -1, -1, -1
	}, {
			-1, 1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1
	}, {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1
	}
	};
	private char MARK;
	private char NYO;
	private char SA;
	private char SRAAA;
	private char SRAAU;
	private char SRAE;
	private char SRAIE;
	private char SRAII;
	private char SRAOE;
	private char SRAOO;
	private char SRAU;
	private char SRAYA;
	private char TRIISAP;
	private char YO;
	
	public KhmerLigaturizer() {
		SRAAA = UnicodeUtil.unicodeChar("0x17B6");
		SRAE = UnicodeUtil.unicodeChar("0x17C1");
		SRAOE = UnicodeUtil.unicodeChar("0x17BE");
		SRAOO = UnicodeUtil.unicodeChar("0x17C4");
		SRAYA = UnicodeUtil.unicodeChar("0x17BF");
		SRAIE = UnicodeUtil.unicodeChar("0x17C0");
		SRAAU = UnicodeUtil.unicodeChar("0x17C5");
		SRAII = UnicodeUtil.unicodeChar("0x17B8");
		SRAU = UnicodeUtil.unicodeChar("0x17BB");
		TRIISAP = UnicodeUtil.unicodeChar("0x17CA");
		NYO = UnicodeUtil.unicodeChar("0x1789");
		BA = UnicodeUtil.unicodeChar("0x1794");
		YO = UnicodeUtil.unicodeChar("0x1799");
		SA = UnicodeUtil.unicodeChar("0x179F");
		COENG = UnicodeUtil.unicodeChar("0x17D2");
		CORO = Character.toString(UnicodeUtil.unicodeChar("0x17D2")).concat(Character.toString(UnicodeUtil.unicodeChar("0x179A")));
		CONYO = Character.toString(UnicodeUtil.unicodeChar("0x17D2")).concat(Character.toString(UnicodeUtil.unicodeChar("0x1789")));
		MARK = UnicodeUtil.unicodeChar("0x17EA");
	}
	
	private char strEcombining(final char chrInput) {
		char retChar = ' ';
		if (chrInput == SRAOE) {
			retChar = SRAII;
		}
		else if (chrInput == SRAYA) {
			retChar = SRAYA;
		}
		else if (chrInput == SRAIE) {
			retChar = SRAIE;
		}
		else if (chrInput == SRAOO) {
			retChar = SRAAA;
		}
		else if (chrInput == SRAAU) {
			retChar = SRAAU;
		}
		
		return retChar;
	}
	
	// Gets the charactor class.
	private int getCharClass(final char uniChar) {
		int retValue = 0;
		int ch;
		ch = uniChar;
		if (ch > 255) {
			if (ch >= UnicodeUtil.unicodeChar("0x1780")) {
				ch -= UnicodeUtil.unicodeChar("0x1780");
				if (ch < khmerCharClasses.length) {
					retValue = khmerCharClasses[ch];
				}
			}
		}
		return retValue;
	}
	
	/**
	 * Re-order Khmer unicode for display with Khmer.ttf file on Android.
	 * @param strInput Khmer unicode string.
	 * @return String after render.
	 */
	public String render(final String strInput) {
		//Given an input String of unicode cluster to reorder.
		//The return is the visual based cluster (legacy style) String.
		
		int cursor = 0;
		short state = 0;
		int charCount = strInput.length();
		StringBuilder result = new StringBuilder();
		
		while (cursor < charCount) {
			String _reserved = "";
			String _signAbove = "";
			String _signAfter = "";
			String _base = "";
			String _robat = "";
			String _shifter = "";
			String _vowelBefore = "";
			String _vowelBelow = "";
			String _vowelAbove = "";
			String _vowelAfter = "";
			boolean _coeng = false;
			String _cluster;
			
			String _coeng1 = "";
			String _coeng2 = "";
			
			boolean _shifterAfterCoeng = false;
			
			while (cursor < charCount) {
				char curChar = strInput.charAt(cursor);
				int kChar = getCharClass(curChar);
				int charClass = kChar & CF_CLASS_MASK;
				try {
					state = khmerStateTable[state][charClass];
				}
				catch (Exception ex) {
					state = -1;
				}
				
				if (state < 0) {
					break;
				}
				
				//collect variable for cluster here
				
				if (kChar == _xx) {
					_reserved = Character.toString(curChar);
				}
				else if (kChar == _sa) //Sign placed above the base
				{
					_signAbove = Character.toString(curChar);
				}
				else if (kChar == _sp) //Sign placed after the base
				{
					_signAfter = Character.toString(curChar);
				}
				else if (kChar == _c1 || kChar == _c2 || kChar == _c3) //Consonant
				{
					if (_coeng) {
						if ("".equalsIgnoreCase(_coeng1)) {
							_coeng1 = Character.toString(COENG).concat(Character.toString(curChar));
						}
						else {
							_coeng2 = Character.toString(COENG).concat(Character.toString(curChar));
						}
						_coeng = false;
					}
					else {
						_base = Character.toString(curChar);
					}
				}
				else if (kChar == _rb) //Khmer sign robat u17CC
				{
					_robat = Character.toString(curChar);
				}
				else if (kChar == _cs) //Consonant-shifter
				{
					if (!"".equalsIgnoreCase(_coeng1)) {
						_shifterAfterCoeng = true;
					}
					
					_shifter = Character.toString(curChar);
				}
				else if (kChar == _dl) //Dependent vowel placed before the base
				{
					_vowelBefore = Character.toString(curChar);
				}
				else if (kChar == _db) //Dependent vowel placed below the base
				{
					_vowelBelow = Character.toString(curChar);
				}
				else if (kChar == _da) //Dependent vowel placed above the base
				{
					_vowelAbove = Character.toString(curChar);
				}
				else if (kChar == _dr) //Dependent vowel placed behind the base
				{
					_vowelAfter = Character.toString(curChar);
				}
				else if (kChar == _co) //Khmer combining mark COENG
				{
					_coeng = true;
				}
				else if (kChar == _va) //Khmer split vowel, see _da
				{
					_vowelBefore = Character.toString(SRAE);
					_vowelAbove = Character.toString(strEcombining(curChar));
				}
				else if (kChar == _vr) //Khmer split vowel, see _dr
				{
					_vowelBefore = Character.toString(SRAE);
					_vowelAfter = Character.toString(strEcombining(curChar));
				}
				
				cursor += 1;
			}
			// end of while (a cluster has found)
			
			// logic when cluster has coeng
			// should coeng be located on left side
			String _coengBefore = "";
			if (CORO.equalsIgnoreCase(_coeng1)) {
				_coengBefore = _coeng1;
				_coeng1 = "";
			}
			else if (CORO.equalsIgnoreCase(_coeng2)) {
				_coengBefore = _coeng2;
				_coeng2 = "";
			}
			
			if (!"".equalsIgnoreCase(_coeng1) || !"".equalsIgnoreCase(_coeng2)) {
				// NYO must change to other form when there is coeng
				if (Character.toString(NYO).equalsIgnoreCase(_base)) {
					_base = Character.toString(UnicodeUtil.unicodeChar("0xF0AE"));
					
					if (_coeng1.equalsIgnoreCase(CONYO)) {
						_coeng1 = Character.toString(UnicodeUtil.unicodeChar("0xF0CB"));
					}
				}
			}
			
			//logic of shifter with base character
			if (!"".equalsIgnoreCase(_base) && !"".equalsIgnoreCase(_shifter)) {
				//special case apply to BA only
				if (!"".equalsIgnoreCase(_vowelAbove) && Character.toString(BA).equalsIgnoreCase(_base) && Character.toString(TRIISAP).equalsIgnoreCase(_shifter)) {
					_vowelAbove = UnicodeUtil.getVowelAbove(_vowelAbove);
				}
				else if (!"".equalsIgnoreCase(_vowelAbove)) {
					_shifter = "";
					_vowelBelow = Character.toString(SRAU);
				}
			}
			
			// uncomplete coeng
			if (_coeng && "".equalsIgnoreCase(_coeng1)) {
				_coeng1 = Character.toString(COENG);
			}
			else if (_coeng && "".equalsIgnoreCase(_coeng2)) {
				_coeng2 = Character.toString(MARK).concat(Character.toString(COENG));
			}
			
			//render DOTCIRCLE for standalone sign or vowel
			if ("".equalsIgnoreCase(_base) && ("".equalsIgnoreCase(_vowelBefore) || "".equalsIgnoreCase(_coengBefore) || !"".equalsIgnoreCase(_robat) || !"".equalsIgnoreCase(_shifter) || !"".equalsIgnoreCase(_coeng1) || !"".equalsIgnoreCase(_coeng2) || !"".equalsIgnoreCase(_vowelAfter) || !"".equalsIgnoreCase(_vowelBelow) || !"".equalsIgnoreCase(_vowelAbove) || !"".equalsIgnoreCase(_signAbove) || !"".equalsIgnoreCase(_signAfter))) {
				//_base = ""; //DOTCIRCLE
			}
			
			//place of shifter
			String _shifter1 = "";
			String _shifter2 = "";
			_shifter = UnicodeUtil.getConsonantShifter(_shifter);
			
			if (_shifterAfterCoeng) {
				_shifter2 = _shifter;
			}
			else {
				_shifter1 = _shifter;
			}
			
			boolean _specialCaseBA = false;
			String strMARK_SRAAA = Character.toString(MARK).concat(Character.toString(SRAAA));
			String strMARK_SRAAU = Character.toString(MARK).concat(Character.toString(SRAAU));
			
			if (Character.toString(BA).equalsIgnoreCase(_base) && (Character.toString(SRAAA).equalsIgnoreCase(_vowelAfter) || Character.toString(SRAAU).equalsIgnoreCase(_vowelAfter) || strMARK_SRAAA.equalsIgnoreCase(_vowelAfter) || strMARK_SRAAU.equalsIgnoreCase(_vowelAfter))) {
				// SRAAA or SRAAU will get a MARK if there is coeng, redefine to last char
				_base = Character.toString(UnicodeUtil.unicodeChar("0xF0AF"));
				_specialCaseBA = true;
				
				if (!"".equalsIgnoreCase(_coeng1)) {
					String _coeng1Complete = _coeng1.substring(0, _coeng1.length() - 1);
					if (Character.toString(BA).equalsIgnoreCase(_coeng1Complete) || Character.toString(YO).equalsIgnoreCase(_coeng1Complete) || Character.toString(SA).equalsIgnoreCase(_coeng1Complete)) {
						_specialCaseBA = false;
						
					}
				}
			}
			
			_coengBefore = UnicodeUtil.getSubConsonant(_coengBefore);
			_coeng1 = UnicodeUtil.getSubConsonant(_coeng1);
			_coeng2 = UnicodeUtil.getSubConsonant(_coeng2);
			_vowelAfter = UnicodeUtil.change(_vowelAfter);
			_signAbove = UnicodeUtil.change(_signAbove);
			
			if (!_robat.equalsIgnoreCase("")) {
				_vowelAbove = UnicodeUtil.getVowelAbove(_vowelAbove);
			}
			else {
				_vowelAbove = UnicodeUtil.change(_vowelAbove);
			}
			
			if (!_coeng1.equalsIgnoreCase("") || !_coeng2.equalsIgnoreCase("")) {
				_vowelBelow = UnicodeUtil.getVowelBelow(_vowelBelow);
			}
			else {
				_vowelBelow = UnicodeUtil.change(_vowelBelow);
			}
			
			// cluster formation
			if (_specialCaseBA) {
				_cluster = _vowelBefore + _coengBefore + _base + _vowelAfter + _robat + _shifter1 + _coeng1 + _coeng2 + _shifter2 + _vowelBelow + _vowelAbove + _signAbove + _signAfter;
			}
			else {
				_cluster = _vowelBefore + _coengBefore + _base + _robat + _shifter1 + _coeng1 + _coeng2 + _shifter2 + _vowelBelow + _vowelAbove + _vowelAfter + _signAbove + _signAfter;
			}
			//            + UnicodeUtil.unicodeChar("0x200B")
			result.append(_cluster + _reserved);
			state = 0;
			//end of while
		}
		
		return result.toString();
	}
	
	@Override
	public String process(String s) {
		System.out.println("works");
		KhmerLigaturizer khmerRender = new KhmerLigaturizer();
		return BidiLine.processLTR(khmerRender.render(s), PdfWriter.RUN_DIRECTION_LTR, 0);
	}
	
	@Override
	public boolean isRTL() {
		return false;
	}
}



class UnicodeUtil {
	
	private static final String COENG = Character.toString(UnicodeUtil.unicodeChar("0x17D2"));
	private static Map<String, String> subConsonant;
	private static Map<String, String> vowelBelow;
	private static Map<String, String> vowelAbove;
	private static Map<String, String> changeVowel;
	private static Map<String, String> consonantShifter;
	
	public static char unicodeChar(final String strInput) {
		return (char) (Integer.parseInt(strInput.substring(2, strInput.length()), 16));
	}
	
	/**
	 * Get subscription consonant key.
	 * @param charValue char value.
	 * @return Subscription consonant key
	 */
	private static String getSubConsonantKey(final char charValue) {
		return COENG.concat(Character.toString(charValue));
	}
	
	/**
	 * Get khmer key.
	 * @param charValue char key.
	 * @return Khmer key.
	 */
	private static String getKey(final char charValue) {
		return Character.toString(charValue);
	}
	
	/**
	 * Get Khmer vowel below by key.
	 * @param key Key as string.
	 * @return Khmer vowel below.
	 */
	public static String getVowelBelow(final String key) {
		if (vowelBelow == null) {
			loadVowelBelow();
		}
		if (vowelBelow.containsKey(key)) {
			return Character.toString(UnicodeUtil.unicodeChar(vowelBelow.get(key)));
		}
		return key;
	}
	
	/**
	 * Get Khmer vowel above by key.
	 * @param key Key as string.
	 * @return Khmer vowel above.
	 */
	public static String getVowelAbove(final String key) {
		if (vowelAbove == null) {
			loadVowelAbove();
		}
		if (vowelAbove.containsKey(key)) {
			return Character.toString(UnicodeUtil.unicodeChar(vowelAbove.get(key)));
		}
		return key;
	}
	
	/**
	 * Get Khmer change vowel by key.
	 * @param key Key as string.
	 * @return Khmer change vowel.
	 */
	public static String change(final String key) {
		if (changeVowel == null) {
			loadChangeVowel();
		}
		if (changeVowel.containsKey(key)) {
			return Character.toString(UnicodeUtil.unicodeChar(changeVowel.get(key)));
		}
		return key;
	}
	
	public static String getConsonantShifter(final String key) {
		if (consonantShifter == null) {
			consonantShifter = new LinkedHashMap<String, String>();
			consonantShifter.put(getKey(UnicodeUtil.unicodeChar("0x17C9")), "0xF0D4");
			consonantShifter.put(getKey(UnicodeUtil.unicodeChar("0x17CA")), "0xF0DB");
		}
		if (consonantShifter.containsKey(key)) {
			return Character.toString(UnicodeUtil.unicodeChar(consonantShifter.get(key)));
		}
		return key;
	}
	
	/**
	 * Get Khmer subscription consonant.
	 * @param key Subscription consonant key.
	 * @return Khmer subscription consonant.
	 */
	public static String getSubConsonant(final String key) {
		if (subConsonant == null) {
			loadConsonant();
		}
		if (subConsonant.containsKey(key)) {
			return Character.toString(UnicodeUtil.unicodeChar(subConsonant.get(key)));
		}
		return key;
	}
	
	/**
	 * Load subscription by Consonant.
	 */
	private static void loadConsonant() {
		subConsonant = new LinkedHashMap<String, String>();
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1780")), "0xF000");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1781")), "0xF001");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1782")), "0xF002");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1783")), "0xF003");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1784")), "0xF004");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1785")), "0xF005");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1786")), "0xF006");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1787")), "0xF007");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1788")), "0xF008");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1789")), "0xF009");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x178A")), "0xF00A");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x178B")), "0xF00B");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x178C")), "0xF00C");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x178D")), "0xF00D");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x178E")), "0xF00E");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x178F")), "0xF00F");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1790")), "0xF010");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1791")), "0xF011");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1792")), "0xF012");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1793")), "0xF013");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1794")), "0xF014");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1795")), "0xF015");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1796")), "0xF016");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1797")), "0xF017");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1798")), "0xF018");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x1799")), "0xF019");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x179A")), "0xF01A");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x179B")), "0xF01B");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x179C")), "0xF01C");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x179F")), "0xF01F");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x17A0")), "0xF0A0");
		subConsonant.put(getSubConsonantKey(UnicodeUtil.unicodeChar("0x17A2")), "0xF0A2");
	}
	
	/**
	 * Load vowel below.
	 */
	private static void loadVowelBelow() {
		vowelBelow = new LinkedHashMap<String, String>();
		vowelBelow.put(getKey(UnicodeUtil.unicodeChar("0x17BB")), "0xF0A3");
		vowelBelow.put(getKey(UnicodeUtil.unicodeChar("0x17BC")), "0xF0A4");
		vowelBelow.put(getKey(UnicodeUtil.unicodeChar("0x17BD")), "0xF0A5");
	}
	
	/**
	 * Load vowel above.
	 */
	private static void loadVowelAbove() {
		vowelAbove = new LinkedHashMap<String, String>();
		vowelAbove.put(getKey(UnicodeUtil.unicodeChar("0x17B7")), "0xF0A6");
		vowelAbove.put(getKey(UnicodeUtil.unicodeChar("0x17B8")), "0xF0A7");
		vowelAbove.put(getKey(UnicodeUtil.unicodeChar("0x17B9")), "0xF0A8");
		vowelAbove.put(getKey(UnicodeUtil.unicodeChar("0x17BA")), "0xF0A9");
	}
	
	/**
	 * Load change vowel.
	 */
	private static void loadChangeVowel() {
		changeVowel = new LinkedHashMap<String, String>();
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17B7")), "0xF0CD"); //áž·
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17B8")), "0xF0CE"); //áž¸
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17B9")), "0xF0CF"); //áž¹
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17BA")), "0xF0D0"); //ážº
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17BB")), "0xF0DC"); //áž»
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17BC")), "0xF0DD"); //áž¼
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17BD")), "0xF0DE"); //áž½
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17C5")), "0xF0CC"); //áŸ…
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17C6")), "0xF0D3"); //áŸ†
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17BF")), "0xF0D1"); //áŸ€
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17C0")), "0xF0D2"); //áž¿
		
		//Sign Above
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17C9")), "0xF0D4"); //áŸ‰
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17CB")), "0xF0D5"); //áŸ‹
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17CC")), "0xF0D6"); //áŸŒ
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17CD")), "0xF0D7"); //áŸ�
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17CE")), "0xF0D8"); //
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17CF")), "0xF0D9"); //áŸ�
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17D0")), "0xF0DA"); //áŸ�
		changeVowel.put(getKey(UnicodeUtil.unicodeChar("0x17CA")), "0xF0DB"); //áŸŠ
	}
}
