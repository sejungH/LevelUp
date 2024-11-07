package com.levelup.chat;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;

public class RomanToKorean {

	// 코드타입 - 초성, 중성, 종성
	enum CodeType {
		chosung, jungsung, jongsung
	}

	public static class RomanChar {
		private List<String> possibleList;

		public RomanChar(List<String> list) {
			possibleList = list;
		}

		public boolean isMyball(String eng) {
			if (possibleList == null) {
				return false;
			}
			for (String s : possibleList) {
				if (s.equals(eng)) {
					return true;
				}
			}
			return false;
		}
	}

	private static final String jauems = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
	private static final String mouems = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
	private static final char quote = '\'';

	// 초성
	private static final List<RomanChar> initial = Arrays.asList(new RomanChar(Arrays.asList("r")),
			new RomanChar(Arrays.asList("R")), new RomanChar(Arrays.asList("s", "S")),
			new RomanChar(Arrays.asList("e")), new RomanChar(Arrays.asList("E")),
			new RomanChar(Arrays.asList("f", "F")), new RomanChar(Arrays.asList("a", "A")),
			new RomanChar(Arrays.asList("q")), new RomanChar(Arrays.asList("Q")), new RomanChar(Arrays.asList("t")),
			new RomanChar(Arrays.asList("T")), new RomanChar(Arrays.asList("d", "D")),
			new RomanChar(Arrays.asList("w")), new RomanChar(Arrays.asList("W")),
			new RomanChar(Arrays.asList("c", "C")), new RomanChar(Arrays.asList("z", "Z")),
			new RomanChar(Arrays.asList("x", "X")), new RomanChar(Arrays.asList("v", "V")),
			new RomanChar(Arrays.asList("g", "G"))

	);
	// 중성
	private static final List<RomanChar> mid = Arrays.asList(new RomanChar(Arrays.asList("k", "K")),
			new RomanChar(Arrays.asList("o")), new RomanChar(Arrays.asList("i", "I")),
			new RomanChar(Arrays.asList("O")), new RomanChar(Arrays.asList("j", "J")),
			new RomanChar(Arrays.asList("p")), new RomanChar(Arrays.asList("u", "U")),
			new RomanChar(Arrays.asList("P")), new RomanChar(Arrays.asList("h", "H")),
			new RomanChar(Arrays.asList("hk", "HK", "hK", "Hk")), new RomanChar(Arrays.asList("ho", "Ho")),
			new RomanChar(Arrays.asList("hl", "HL", "Hl", "hL")), new RomanChar(Arrays.asList("y", "Y")),
			new RomanChar(Arrays.asList("n", "N")), new RomanChar(Arrays.asList("nj", "NJ", "nJ", "Nj")),
			new RomanChar(Arrays.asList("np", "Np")), new RomanChar(Arrays.asList("nl", "NL", "Nl", "nL")),
			new RomanChar(Arrays.asList("b", "B")), new RomanChar(Arrays.asList("m", "M")),
			new RomanChar(Arrays.asList("ml", "ML", "Ml", "mL")), new RomanChar(Arrays.asList("l", "L")));
	// 종성
	private static final List<RomanChar> fin = Arrays.asList(new RomanChar(Arrays.asList("r")),
			new RomanChar(Arrays.asList("R")), new RomanChar(Arrays.asList("rt")),
			new RomanChar(Arrays.asList("s", "S")), new RomanChar(Arrays.asList("sw", "Sw")),
			new RomanChar(Arrays.asList("sg", "SG", "Sg", "sG")), new RomanChar(Arrays.asList("e")),
			new RomanChar(Arrays.asList("f", "F")), new RomanChar(Arrays.asList("fr", "Fr")),
			new RomanChar(Arrays.asList("fa", "Fa", "FA", "fA")), new RomanChar(Arrays.asList("fq", "Fq")),
			new RomanChar(Arrays.asList("ft", "Ft")), new RomanChar(Arrays.asList("fx", "Fx", "FX", "fX")),
			new RomanChar(Arrays.asList("fv", "FV", "Fv", "fV")), new RomanChar(Arrays.asList("fg", "FG", "Fg", "fG")),
			new RomanChar(Arrays.asList("a", "A")), new RomanChar(Arrays.asList("q")),
			new RomanChar(Arrays.asList("qt")), new RomanChar(Arrays.asList("t")), new RomanChar(Arrays.asList("T")),
			new RomanChar(Arrays.asList("d", "D")), new RomanChar(Arrays.asList("w")),
			new RomanChar(Arrays.asList("c", "C")), new RomanChar(Arrays.asList("z", "Z")),
			new RomanChar(Arrays.asList("x", "X")), new RomanChar(Arrays.asList("v", "V")),
			new RomanChar(Arrays.asList("g", "G")));

	private static boolean isAlpha(String name) {
		return name.matches("[a-zA-Z]+");
	}

	/**
	 * 영어를 한글로...
	 */
	public static String RomToKor(String eng) {
		StringBuffer sb = new StringBuffer();
		int initialCode = 0, medialCode = 0, finalCode = 0;
		int tempMedialCode, tempFinalCode;
		boolean inQuote = false;

		for (int i = 0; i < eng.length(); i++) {
			char ch = eng.charAt(i);
			if (ch == quote) {
				inQuote = !inQuote;

			} else if (inQuote) {
				sb.append(ch);

			} else {
				// 특수문자를 검사
				if (!isAlpha(eng.substring(i, i + 1))) {
					sb.append(eng.substring(i, i + 1));
					continue;
				}

				// 초성코드 추출
				initialCode = getCode(CodeType.chosung, eng.substring(i, i + 1));
				if (initialCode >= 0) {
					i++; // 다음문자로
				}

				// 중성코드 추출
				tempMedialCode = getDoubleMedial(i, eng); // 두 자로 이루어진 중성코드 추출
				if (tempMedialCode >= 0) {
					medialCode = tempMedialCode;
					i += 2;
				} else { // 없다면,
					medialCode = getSingleMedial(i, eng); // 한 자로 이루어진 중성코드 추출
					if (medialCode >= 0) {
						i++;
					}
				}

				// 종성코드 추출
				if (initialCode < 0 || medialCode < 0) {
					// 종성코드고 자시고 초성이나 중성이 없으면 소용이 없다
					i--;
					finalCode = -1;
				} else {
					tempFinalCode = getDoubleFinal(i, eng); // 두 자로 이루어진 종성코드 추출
					if (tempFinalCode >= 0) {
						finalCode = tempFinalCode;
						// 그 다음의 중성 문자에 대한 코드를 추출한다
						tempMedialCode = getSingleMedial(i + 2, eng);
						if (tempMedialCode >= 0) { // 코드 값이 있을 경우
							finalCode = getSingleFinal(i, eng); // 종성 코드 값을 저장한다
						} else {
							i++;
						}
					} else { // 코드 값이 없을 경우 ,
						tempMedialCode = getSingleMedial(i + 1, eng); // 그 다음의 중성 문자에 대한 코드 추출.
						if (tempMedialCode >= 0) { // 그 다음에 중성 문자가 존재할 경우,
							finalCode = -1; // 종성 문자는 없음.
							i--;
						} else {
							if (i < eng.length() && !isAlpha(eng.substring(i, i + 1))) { // 다음글자가 특수문자일 경우 종성문자는 없음
								finalCode = -1;
								i--;
							}
							finalCode = getSingleFinal(i, eng); // 종성 문자 추출
						}
					}
				}
				if (initialCode >= 0 && medialCode >= 0) {
					// 추출한 초성 문자 코드, 중성 문자 코드, 종성 문자 코드를 합한 후 변환하여 스트링버퍼에 넘김
					sb.append((char) (0xAC00 + initialCode * 21 * 28 + medialCode * 28 + finalCode + 1));
				} else if (initialCode >= 0 && medialCode < 0) {
					// 초성만
					sb.append(jauems.substring(initialCode, initialCode + 1));
				} else if (initialCode < 0 && medialCode >= 0) {
					// 중성만
					sb.append(mouems.substring(medialCode, medialCode + 1));
				} else {
					// 존재하나 이런경우가??
					sb.append(eng.substring(i, i + 1));
				}
			}
		}

		return sb.toString();
	}

	/**
	 * EngChar리스트에서 String에 해당하는 EngChar의 index를 리턴한다
	 *
	 * @param list EngChar리스트
	 * @param c    해당 문자
	 */
	private static int getEngCharIndex(List<RomanChar> list, String c) {
		if (list == null) {
			return -1;
		}
		for (int i = 0; i < list.size(); i++) {
			RomanChar ec = list.get(i);
			if (ec.isMyball(c)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 해당 문자에 따른 코드를 추출한다
	 *
	 * @param type 초성 : chosung, 중성 : jungsung, 종성 : jongsung 구분
	 * @param char 해당 문자
	 */
	private static int getCode(CodeType type, String c) {
		switch (type) {
		case chosung:
			return getEngCharIndex(initial, c);
		case jungsung:
			return getEngCharIndex(mid, c);
		case jongsung:
			return getEngCharIndex(fin, c);
		default:
			Bukkit.getLogger().warning("잘못된 타입 입니다");
		}

		return -1;
	}

	// 한 자로 된 중성값을 리턴한다
	// 인덱스를 벗어낫다면 -1을 리턴
	private static int getSingleMedial(int i, String eng) {
		if ((i + 1) <= eng.length()) {
			return getCode(CodeType.jungsung, eng.substring(i, i + 1));
		} else {
			return -1;
		}
	}

	// 두 자로 된 중성을 체크하고, 있다면 값을 리턴한다
	// 없으면 리턴값은 -1
	private static int getDoubleMedial(int i, String eng) {
		if ((i + 2) > eng.length()) {
			return -1;
		} else {
			return getCode(CodeType.jungsung, eng.substring(i, i + 2));
		}
	}

	// 한 자로된 종성값을 리턴한다
	// 인덱스를 벗어낫다면 -1을 리턴
	private static int getSingleFinal(int i, String eng) {
		if ((i + 1) <= eng.length()) {
			return getCode(CodeType.jongsung, eng.substring(i, i + 1));
		} else {
			return -1;
		}
	}

	// 두 자로된 종성을 체크하고, 있다면 값을 리턴한다
	// 없으면 리턴값은 -1
	private static int getDoubleFinal(int i, String eng) {
		if ((i + 2) > eng.length()) {
			return -1;
		} else {
			return getCode(CodeType.jongsung, eng.substring(i, i + 2));
		}
	}
}