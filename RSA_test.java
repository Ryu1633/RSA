package study;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.math.BigInteger;

import java.util.Random;
import java.util.Scanner;

public class RSA_test {
	
	static boolean check = true;	//키 생성 성공여부 확인용 0 = false, 1 = true
	static byte[] text_byte;		//암호화, 복호화 할 데이터를 저장할 바이트 배열
	
	static BigInteger prime_number_p = new BigInteger("0");	//소수 p
	static BigInteger prime_number_q = new BigInteger("0");	//소수 q
	static BigInteger n = new BigInteger("0");					//공개키, 개인키 n
	static BigInteger o = new BigInteger("0");					//오일러 함수 o
	static BigInteger e = new BigInteger("0");					//공개키 e
	static BigInteger d = new BigInteger("0");					//개인키 d
	
	static void RSA() {
		while(check) {
			prime_number_p = Create_Prime_Number(125);		//소수 p 생성
			prime_number_q = Create_Prime_Number(125);		//소수 q 생성
			n = prime_number_p.multiply(prime_number_q);	//n계산 (n = p * q)
			
			//오일러 함수(p-1)(q-1)계산
			o = prime_number_p.subtract(new BigInteger("1")).multiply(prime_number_q.subtract(new BigInteger("1")));
			
			e = n.subtract(o);	//공개키 e 생성, 결과 출력을 빠르게 하기위해 n - o값을 사용
			
			//개인키 d 생성
			d = Extended_Euclidean_Algorithm(n, o, new BigInteger("1"), new BigInteger("0"),
					new BigInteger("0"), new BigInteger("1"));
			
			//개인키 d값이 음수인 경우 o값을 더해서 양수로 만듬
			if(d.min(new BigInteger("1")).equals(d))
				d = d.add(o);
		}
		
		System.out.println("소수p\t: " + prime_number_p);
		System.out.println("소수q\t: " + prime_number_q);
		System.out.println("o\t: " + o);
		System.out.println("n\t: " + n);
		System.out.println("공개키 e\t: " + e);
		System.out.println("개인키 d\t: " + d);
		System.out.println("n의 비트 수: " + n.bitLength());
		System.out.println("한번에 입력 가능한 문자 수: " + n.bitLength() / 8);
	}
	
	//확률론적 방법으로 큰 소수를 생성(밀러-라빈 소수판별법 사용)
	static BigInteger Create_Prime_Number(int len) {
		Random random = new Random();
		
		BigInteger prime_number = BigInteger.probablePrime(len, random);	//2^len 길이의 소수를 선택
		return prime_number;	//소수 반환
		}
	
	//확장 유클리드 알고리즘(빠르게 개인키 d값을 계산)
	static BigInteger Extended_Euclidean_Algorithm(BigInteger r1, BigInteger r2, BigInteger s1, BigInteger s2,
			BigInteger t1, BigInteger t2) {
		
		BigInteger q = r1.divide(r2);
		BigInteger s = s2.multiply(q).subtract(s1);
		BigInteger t = t2.multiply(q).subtract(t1);
		BigInteger r = r2.multiply(q).subtract(r1);
		
		if(r.intValue() == 0) {
			s = s2;
			t = t2;
			
			if(r2.intValue() == 1)
					check = false;

			return s;
		}
		else
			return Extended_Euclidean_Algorithm(r2, r, s2, s, t2, t);
	}
	
	//원본 파일 생성
	static void Create_File() {
		Scanner stdIn = new Scanner(System.in);
		
		System.out.print("\n원본 데이터 입력(한글x, 띄어쓰기x): ");
		String text = stdIn.next();
		text_byte = text.getBytes();
		
		try(FileWriter out = new FileWriter("C:\\Users\\Ryu\\Desktop\\원본.txt")) {
			out.write(new String(text_byte));
			
		}	catch(IOException err) {
			err.printStackTrace();
		}
		stdIn.close();
	}
	
	//암호화 파일 생성
	static void encryption() {
		try (BufferedReader in = new BufferedReader(new FileReader("C:\\Users\\Ryu\\Desktop\\원본.txt"));
				PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\Ryu\\Desktop\\암호화.txt"))) {
			
			String s = null;
			
			while((s = in.readLine()) != null) {
				text_byte = s.getBytes();
				BigInteger c = new BigInteger(text_byte);
				
				System.out.println("원본 바이트: " + c);
				System.out.println("원본 데이터: " + new String(c.toByteArray()));
				
				c = c.modPow(e, n);	//암호화
				
				System.out.println("암호화 바이트: " + c);
				System.out.println("암호화 데이터: " + new String(c.toByteArray()));
				
				text_byte = c.toByteArray();
				out.println(new String(c.toByteArray()));
			}
		}	catch(IOException err2) {
			err2.printStackTrace();
		}
	}
	
	//복호화 파일 생성
	static void decryption() {
		try (PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\Ryu\\Desktop\\복호화.txt"))) {
			
				BigInteger c = new BigInteger(text_byte);
				
				c = c.modPow(d, n);	//복호화
				
				System.out.println("복호화 바이트: " + c);
				System.out.println("복호화 데이터: " + new String(c.toByteArray()));
				
				out.println(new String(c.toByteArray()));
		}	catch(IOException err2) {
			err2.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		RSA();
		Create_File();
		encryption();
		decryption();
	}
}
