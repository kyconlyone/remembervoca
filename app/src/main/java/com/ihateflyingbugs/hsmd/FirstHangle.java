package com.ihateflyingbugs.hsmd;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;



////////////////////////////////////////////////////////////////////////
// �쒓� 臾몄옄�댁쓣 珥덉꽦/以묒꽦/醫낆꽦�쇰줈 遺꾨━�섏뿬 異쒕젰 (UTF-8 踰꾩쟾)
// v1.0
////////////////////////////////////////////////////////////////////////

public class FirstHangle {

	// ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��
	final static char[] ChoSung   = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
	// ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��
	final static char[] JwungSung = { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };
	//         ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��     ��
	final static char[] JongSung  = { 0,      0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };




	public static void main(String[] args) {

		Log.e("firstHangle", "asdf    ");
		if (args.length == 0) {        // args.length ���듭뀡 媛쒖닔
			System.err.println("�먯냼 遺꾨━���뚯씪���대쫫���낅젰�섏꽭��..");
			System.exit(1);              // �쎌쓣 �뚯씪紐낆쓣 二쇱� �딆븯���뚮뒗 醫낅즺
		}


		try {
			////////////////////////////////////////////////////////////////
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(args[0]),
							"UTF-8"
					)
			);


			BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream("out.txt"),
							"UTF-8"
					)
			);


			String s;
			while ((s = in.readLine()) != null) {
				out.write(hangulToJaso(s)); out.newLine();
			}

			in.close(); out.close();
			////////////////////////////////////////////////////////////////
		} catch (IOException e) {
			System.err.println(e); // �먮윭媛��덈떎硫�硫붿떆吏�異쒕젰
			System.exit(1);
		}


	} // main()


	public static String hangulToJaso(String s) { // �좊땲肄붾뱶 �쒓� 臾몄옄�댁쓣 �낅젰 諛쏆쓬
		int a, b, c; // �먯냼 踰꾪띁: 珥덉꽦/以묒꽦/醫낆꽦 ��
		String result = "";

		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);

			if (ch >= 0xAC00 && ch <= 0xD7A3) { // "AC00:媛� ~ "D7A3:�� ���랁븳 湲�옄硫�遺꾪빐

				c = ch - 0xAC00;
				a = c / (21 * 28);
				c = c % (21 * 28);
				b = c / 28;
				c = c % 28;

				result = result + ChoSung[a];
				//if (c != 0) result = result + JongSung[c] ; // c媛�0���꾨땲硫� 利�諛쏆묠���덉쑝硫�
			} else {
				result = result + ch;
			}
		}
		return result;
	}


}