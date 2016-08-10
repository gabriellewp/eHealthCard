package readwritets1;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;
import javacardx.apdu.ExtendedLength;

public class ReadWriteTS1 extends Applet implements ExtendedLength {

	// constants
	private static final byte RW_CLA = (byte) 0x80;
	private static final byte INS_READ = (byte) 0x00;
	private static final byte INS_WRITE = (byte) 0x01;
	private static final byte INS_CLEAR = (byte) 0x02;
	private static final byte INS_READ_ARRAY = (byte) 0x03;
	private static final byte INS_WRITE_ARRAY = (byte) 0x04;
	private static final byte INS_CLEAR_ARRAY = (byte) 0x05;
	private static final byte INS_INSERT_MEDREC_DINAMIC = (byte)0x06;
	private static final byte INS_SORT_ADDRESS = (byte)0x07;
	private static final byte INS_INSERT_UPDATE = (byte)0x08;
	private static final byte INS_TABLE_MANAGEMENT = (byte)0x09;
	private static final byte INS_GET_VALID_INDEXS = (byte)0x10;
	private static final byte INS_PREV_CHECKING = (byte)0x11;
	private static final byte INS_READ_TIMESTAMP = (byte) 0x12;
	private static final byte INS_WRITE_TIMESTAMP = (byte)0x13;
	private static final byte INS_GREATEST_TIMESTAMP = (byte)0x14;
	private static final byte INS_COMMAND_ONLY = (byte)0x15;
	
	
	// variables
	private Object[] storageOffset = new Object[(short)5];
	private Object[] storage;// = new Object[(short) 9];
	private short mdCapacity;
	private ReadWriteTS1() {
		mdCapacity =5;
		byte[] dataPasien, medrecStatik,medrecDinamikHeadTail, medRecIdx, addressIdx, statusFlag, errorFlag, backUpMedRecIdx, backUpAddressIdx, backUpStatusFlag, ts2FYear, ts2LYear, tsDate, tsMonth, tsHour, tsMinute, tsSecond; ;
		
		storage = new Object[(short) 35];
		
		dataPasien = new byte[(short) 8652]; //storage 0
		medrecStatik = new byte[(short) 2145]; //storage 1
		//medical record dinamik init
		short i;
		for (i=2; i< (short) (mdCapacity + 2); i++){ //storage 2 to mdCapacity+2
			byte[] medrecDinamik = new byte[(short)2467];
			Util.arrayFillNonAtomic(medrecDinamik, (short) 0, (short) 2467, (byte) 0x00);
			storage[i] = medrecDinamik;
		}
		medrecDinamikHeadTail = new byte[(short)2]; //storage 
		//processFlag = new byte[(short)1];
		medRecIdx = new byte[(short) mdCapacity];
		addressIdx = new byte[(short)mdCapacity];
		statusFlag = new byte[(short)mdCapacity];
		errorFlag = new byte[(short)2];
		backUpMedRecIdx = new byte[(short)mdCapacity];
		backUpAddressIdx = new byte[(short)mdCapacity];
		backUpStatusFlag = new byte[(short)mdCapacity];
		
		for(i=(short)(mdCapacity+10);i<(short)35;i++){
			byte[] timeStamp =  new byte[(short)7];
			Util.arrayFillNonAtomic(timeStamp, (short) 0, (short) 7, (byte) 0x00);
			storage[i] = timeStamp;
		}

		 //2 first digit of year,2 last digit of year, date, month, hour, minute, second
		
		Util.arrayFillNonAtomic(dataPasien, (short) 0, (short) 8652, (byte) 0x00);
		Util.arrayFillNonAtomic(medrecStatik, (short) 0, (short) 2145, (byte) 0x00);
		Util.arrayFillNonAtomic(medrecDinamikHeadTail, (short) 0, (short) 2, (byte)0x00);
		Util.arrayFillNonAtomic(medRecIdx,(short)0,(short)mdCapacity,(byte)0);
		
		//fill addressIdx with increment value of address
		//addressIdx = {'0',0','0,0,0};
		for(i=(byte)0;i<(short)mdCapacity;i++){
			Util.arrayFillNonAtomic(addressIdx,(short)i, (short)1,(byte)i);
		}
		
		Util.arrayFillNonAtomic(statusFlag,(short)0,(short)mdCapacity,(byte)0x00); // 0 = Open, 1 = Close, 2=Nonvalid
		Util.arrayFillNonAtomic(errorFlag,(short)0,(short)2,(byte)0x00); // 0 = Open, 1 = Close, 2=Nonvalid
		Util.arrayFillNonAtomic(backUpMedRecIdx,(short)0,(short)mdCapacity,(byte)0x00);
		Util.arrayFillNonAtomic(backUpAddressIdx,(short)0,(short)mdCapacity,(byte)0x00);
		Util.arrayFillNonAtomic(backUpStatusFlag,(short)0,(short)mdCapacity,(byte)0x00);
		
		storage[0] = dataPasien;
		storage[1] = medrecStatik;
		storage[(short) (mdCapacity +2)] = medrecDinamikHeadTail;
		storage[(short) (mdCapacity +3)] = medRecIdx;
		storage[(short) (mdCapacity +4)] = addressIdx;
		storage[(short) (mdCapacity +5)] = statusFlag;
		storage[(short) (mdCapacity +6)] = errorFlag;
		storage[(short) (mdCapacity +7)] = backUpMedRecIdx;
		storage[(short) (mdCapacity +8)] = backUpAddressIdx;
		storage[(short) (mdCapacity +9)] = backUpStatusFlag;
		//storage ke 10 -15 untuk isi timestamp

		
		short[] dataPasienOffset = new short[(short) 45];
		short[] medrecStatikOffset = new short[(short) 8];
		short[] medrecDinamikOffset0 = new short[(short) 49];
		short[] medrecDinamikHeadTailOffset = new short[(short) 3];
		short[] processFlagOffset = new short[(short)2];
		
		dataPasienOffset[0] = (short) 0; // No Smartcard
		dataPasienOffset[1] = (short) 116; // Kategori Pasien/Jenis pembayaran/Asuansi
		dataPasienOffset[2] = (short) 554; // Nomer Asuransi
		dataPasienOffset[3] = (short) 730; // Tanggal Daftar
		dataPasienOffset[4] = (short) 829; // Nama Pasien
		dataPasienOffset[5] = (short) 1121; // Nama KK
		dataPasienOffset[6] = (short) 1413; // Hubungan Keluarga
		dataPasienOffset[7] = (short) 1510; // Alamat
		dataPasienOffset[8] = (short) 2098; // RT
		dataPasienOffset[9] = (short) 2389; // RW
		dataPasienOffset[10] = (short) 2680; // Kelurahan / Desa
		dataPasienOffset[11] = (short) 2971; // Kecamatan
		dataPasienOffset[12] = (short) 3262; // Kota / Kabupaten
		dataPasienOffset[13] = (short) 3553; // Propinsi
		dataPasienOffset[14] = (short) 3844; // Kode pos
		dataPasienOffset[15] = (short) 4138; // Diluar/Didalam wilayah kerja
		dataPasienOffset[16] = (short) 4235; // Tempat Lahir
		dataPasienOffset[17] = (short) 4351; // Tanggal Lahir
		dataPasienOffset[18] = (short) 4450; // Telepon
		dataPasienOffset[19] = (short) 4562; // HP
		dataPasienOffset[20] = (short) 4674; // No. KTP (ID Number) - NIK
		dataPasienOffset[21] = (short) 4898; // Jenis Kelamin
		dataPasienOffset[22] = (short) 4995; // Agama
		dataPasienOffset[23] = (short) 5286; // Pendidikan
		dataPasienOffset[24] = (short) 5577; // Pekerjaan
		dataPasienOffset[25] = (short) 5868; // Kelas Perawatan
		dataPasienOffset[26] = (short) 6162; // Alamat e-mail
		dataPasienOffset[27] = (short) 6278; // Status perkawinan
		dataPasienOffset[28] = (short) 6569; // Kewarganegaraan
		dataPasienOffset[29] = (short) 6860; // KK Nama
		dataPasienOffset[30] = (short) 7006; // KK Hubungan
		dataPasienOffset[31] = (short) 7103; // KK Alamat
		dataPasienOffset[32] = (short) 7299; // KK Kelurahan / Desa
		dataPasienOffset[33] = (short) 7398; // KK Kecamatan
		dataPasienOffset[34] = (short) 7496; // KK Kota / Kabupaten
		dataPasienOffset[35] = (short) 7594; // KK Propinsi
		dataPasienOffset[36] = (short) 7692; // KK Kode pos
		dataPasienOffset[37] = (short) 7793; // KK Telepon
		dataPasienOffset[38] = (short) 7905; // KK HP
		dataPasienOffset[39] = (short) 8017; // KK Nama Kantor
		dataPasienOffset[40] = (short) 8133; // KK Alamat Kantor
		dataPasienOffset[41] = (short) 8329; // KK Kota / Kabupaten
		dataPasienOffset[42] = (short) 8428; // KK Telepon
		dataPasienOffset[43] = (short) 8540; // KK HP
		dataPasienOffset[44] = (short) 8652; // Dummy
		
		medrecStatikOffset[0] = (short) 0; // Alergi
		medrecStatikOffset[1] = (short) 196; // Golongan darah
		medrecStatikOffset[2] = (short) 390; // Riwayat Operasi
		medrecStatikOffset[3] = (short) 741; // Riwayat Rawat RS
		medrecStatikOffset[4] = (short) 1092; // Riwayat penyakit Kronis (Jantung, Paru, Ginjal, dll.)
		medrecStatikOffset[5] = (short) 1443; // Riwayat penyakit bawaan dan orang tua/keluarga/kerabat
		medrecStatikOffset[6] = (short) 1794; // Faktor Resiko
		medrecStatikOffset[7] = (short) 2145; // Dummy
		
		medrecDinamikOffset0[0] = (short) 0; // No Record (Index)
		medrecDinamikOffset0[1] = (short) 100; // Tanggal periksa
		medrecDinamikOffset0[2] = (short) 110; // Keluhan utama
		medrecDinamikOffset0[3] = (short) 160; // Anamnesa
		medrecDinamikOffset0[4] = (short) 360; // Riwayat Penyakit Dahulu
		medrecDinamikOffset0[5] = (short) 460; // Riwayat Penyakit pada keluarga/kerabat
		medrecDinamikOffset0[6] = (short) 560; // Pemeriksaan Fisik
		medrecDinamikOffset0[7] = (short) 660; // Tinggi
		medrecDinamikOffset0[8] = (short) 664; // Berat badan
		medrecDinamikOffset0[9] = (short) 668; // Systole
		medrecDinamikOffset0[10] = (short) 672; // Diastole
		medrecDinamikOffset0[11] = (short) 676; // Nadi
		medrecDinamikOffset0[12] = (short) 679; // Kesadaran
		medrecDinamikOffset0[13] = (short) 680; // Suhu
		medrecDinamikOffset0[14] = (short) 684; // Respirasi
		medrecDinamikOffset0[15] = (short) 687; // Lain-lain
		medrecDinamikOffset0[16] = (short) 937; // Lab execute flag
		medrecDinamikOffset0[17] = (short) 938; // Expertise Lab/Radio/etc
		medrecDinamikOffset0[18] = (short) 1450; // Catatan Lab
		medrecDinamikOffset0[19] = (short) 1500; // Terapi
		medrecDinamikOffset0[20] = (short) 2012; // Resep
		medrecDinamikOffset0[21] = (short) 2212; // Catatan resep
		medrecDinamikOffset0[22] = (short) 2262; // Eksekusi resep flag
		medrecDinamikOffset0[23] = (short) 2263; // Repetisi resep
		medrecDinamikOffset0[24] = (short) 2264; // Prognosa
		medrecDinamikOffset0[25] = (short) 2265; // Kode Penyakit ICD 1
		medrecDinamikOffset0[26] = (short) 2275; // Kode Penyakit ICD 2
		medrecDinamikOffset0[27] = (short) 2285; // Kode Penyakit ICD 3
		medrecDinamikOffset0[28] = (short) 2295; // Kode Penyakit ICD 4
		medrecDinamikOffset0[29] = (short) 2305; // Kode Penyakit ICD 5
		medrecDinamikOffset0[30] = (short) 2315; // Kode Penyakit ICD 6
		medrecDinamikOffset0[31] = (short) 2325; // Kode Penyakit ICD 7
		medrecDinamikOffset0[32] = (short) 2335; // Kode Penyakit ICD 8
		medrecDinamikOffset0[33] = (short) 2345; // Kode Penyakit ICD 9
		medrecDinamikOffset0[34] = (short) 2355; // Kode Penyakit ICD 10
		medrecDinamikOffset0[35] = (short) 2365; // Kode Penyakit ICD 1 Status Diagnosa
		medrecDinamikOffset0[36] = (short) 2366; // Kode Penyakit ICD 2 Status Diagnosa
		medrecDinamikOffset0[37] = (short) 2367; // Kode Penyakit ICD 3 Status Diagnosa
		medrecDinamikOffset0[38] = (short) 2368; // Kode Penyakit ICD 4 Status Diagnosa
		medrecDinamikOffset0[39] = (short) 2369; // Kode Penyakit ICD 5 Status Diagnosa
		medrecDinamikOffset0[40] = (short) 2370; // Kode Penyakit ICD 6 Status Diagnosa
		medrecDinamikOffset0[41] = (short) 2371; // Kode Penyakit ICD 7 Status Diagnosa
		medrecDinamikOffset0[42] = (short) 2372; // Kode Penyakit ICD 8 Status Diagnosa
		medrecDinamikOffset0[43] = (short) 2373; // Kode Penyakit ICD 9 Status Diagnosa
		medrecDinamikOffset0[44] = (short) 2374; // Kode Penyakit ICD 10 Status Diagnosa
		medrecDinamikOffset0[45] = (short) 2375; // Poli yang dituju
		medrecDinamikOffset0[46] = (short) 2425; // Rujukan/Pengirim penderita
		medrecDinamikOffset0[47] = (short) 2455; // ID Puskesmas
		medrecDinamikOffset0[48] = (short) 2467; // Dummy
		
		medrecDinamikHeadTailOffset[0] = (short) 0; //head
		medrecDinamikHeadTailOffset[1] = (short) 1; //tail
		medrecDinamikHeadTailOffset[2] = (short) 2; //dummy
		
		processFlagOffset[0] = (short)0;
		processFlagOffset[1] = (short)1;
		
		storageOffset[0] = dataPasienOffset;
		storageOffset[1] = medrecStatikOffset;
		storageOffset[2] = medrecDinamikOffset0;
		storageOffset[3] = medrecDinamikHeadTailOffset;
		storageOffset[4] = processFlagOffset;
				
	}

	// install
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new ReadWriteTS1().register(bArray,(short)(bOffset+1),bArray[bOffset]);
	}

	// process
	public void process(APDU apdu) {
		if (selectingApplet())
			return;

		byte[] buffer = apdu.getBuffer();

		if (buffer[ISO7816.OFFSET_CLA] != RW_CLA)
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);

		switch (buffer[ISO7816.OFFSET_INS]) {
		case INS_READ:
			read(apdu);
			return;
		case INS_WRITE:
			write(apdu);
			return;
		case INS_CLEAR:
			clear(apdu);
			return;
		case INS_READ_ARRAY:
			readArray(apdu);
			return;
		case INS_WRITE_ARRAY:
			writeArray(apdu);
			return;
		case INS_CLEAR_ARRAY:
			clearArray(apdu);
			return;
		case INS_INSERT_MEDREC_DINAMIC :
			addMedrec(apdu);
			return;
		case INS_SORT_ADDRESS:
			addressSorted(apdu);
			return;
		case INS_INSERT_UPDATE:
			updateCardData(apdu);
			return;
		case INS_TABLE_MANAGEMENT:
			sortStatusFlag(apdu);
			return;
		case INS_GET_VALID_INDEXS:
			returnValidIndex(apdu);
			return;
		case INS_PREV_CHECKING:
			prevChecking(apdu);
			return;
		case INS_READ_TIMESTAMP:
			readTimeStamp(apdu);
			return;
		case INS_WRITE_TIMESTAMP:
			writeTimeStamp(apdu);
			return;
		case INS_GREATEST_TIMESTAMP:
			findGreatestTimestampIdx(apdu);
			return;
		case INS_COMMAND_ONLY:
			commandOnly(apdu);
			return;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	// method realization
//	private void read(APDU apdu) {
//		byte[] buffer = apdu.getBuffer();
//
//		byte p1 = buffer[ISO7816.OFFSET_P1];
//		if (p1 < 0 || p1 > storage.length)
//			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
//		byte[] src = (byte[]) storage[p1];
//
//		byte p2 = buffer[ISO7816.OFFSET_P2];
//		if (p1 >= (byte) 2  && p1<(byte)7)
//			p1 = (byte) 2;
//		else if(p1 == (byte)7){
//			p1 = (byte)3;
//		}
//		short[] srcOff = (short[]) storageOffset[p1];
//		if (p2 < 0 || p2 > srcOff.length)
//			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
//
//		short offset = srcOff[(short) p2];
//		short nextOffset = srcOff[(short) (p2 + 1)];
//		short length = (short) (nextOffset - offset);
//
//		apdu.setOutgoing();
//        apdu.setOutgoingLength(length);
//        apdu.sendBytesLong(src, offset, length);		
//	}
	
	private void read(APDU apdu) {
		byte[] buffer = apdu.getBuffer();
		byte[] addressIdx = (byte[]) storage[(short) (mdCapacity +4)];
		byte[] statusFlag = (byte[]) storage[(short) (mdCapacity +5)];
		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		byte idxOfmemory = addressIdx[p1];
		byte statusflag = statusFlag[p1];
		
		if(statusflag==(byte)1){
			byte[] srcNV = {(byte)0x49,(byte)0x4E,(byte)0x56,(byte)0x41, (byte)0x4C,(byte)0X49,(byte)0x44};
			short length = (short)7;
			short offset = (short)0;
			apdu.setOutgoing();
	        apdu.setOutgoingLength(length);
	        apdu.sendBytesLong(srcNV, offset, length);
		}else{
			byte[] src = (byte[]) storage[idxOfmemory+2];
			byte p2 = buffer[ISO7816.OFFSET_P2];
//			if (p1 >= (byte) 2  && p1<(byte)7)
//				p1 = (byte) 2;
//			else if(p1 == (byte)7){
//				p1 = (byte)3;
//			}

			short[] srcOff = (short[]) storageOffset[2];
			if (p2 < 0 || p2 > srcOff.length)
				ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);

			short offset = srcOff[(short) p2];
			short nextOffset = srcOff[(short) (p2 + 1)];
			short length = (short) (nextOffset - offset);

			apdu.setOutgoing();
	        apdu.setOutgoingLength(length);
	        apdu.sendBytesLong(src, offset, length);
		}
		
	}
	public void addTimestamp(APDU apdu){
		
	}
	private void write(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		byte[] dst = (byte[]) storage[p1];

		byte p2 = buffer[ISO7816.OFFSET_P2];
		if (p1 >= (byte) 2  && p1<(byte)7)
			p1 = (byte) 2;
		else if(p1 == (byte)7){
			p1 = (byte)3;
		}
		short[] dstOff = (short[]) storageOffset[p1];
		if (p2 < 0 || p2 > dstOff.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);

		short offset = dstOff[(short) p2];
		short nextOffset = dstOff[(short) (p2 + 1)];
		short length = (short) (nextOffset - offset);

		short bytesRead = apdu.setIncomingAndReceive();
		short dataOffset = apdu.getOffsetCdata();
		Util.arrayCopy(buffer, dataOffset, dst, offset, bytesRead);
		short dataLength = apdu.getIncomingLength();
		if (dataLength > length)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		short messageOffset = bytesRead;
		if (bytesRead != dataLength) {
			short received = 0;
			do {
				received = apdu.receiveBytes((short) 0);
				Util.arrayCopyNonAtomic(buffer, (short) 0, dst, messageOffset,
						received);
				messageOffset += received;
			} while (received != 0);
		}
	}

	private void clear(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		byte[] dst = (byte[]) storage[p1];

		byte p2 = buffer[ISO7816.OFFSET_P2];
		if (p1 > (byte) 2)
			p1 = (byte) 2;
		short[] dstOff = (short[]) storageOffset[p1];
		if (p2 < 0 || p2 > dstOff.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);

		short offset = dstOff[(short) p2];
		short nextOffset = dstOff[(short) (p2 + 1)];
		short length = (short) (nextOffset - offset);

		Util.arrayFillNonAtomic(dst, offset, length, (byte) 0);
	}

	private void readArray(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		byte[] src = (byte[]) storage[p1];

		short offset = (short) 0;
		short length = (short) src.length;

		apdu.setOutgoing();
        apdu.setOutgoingLength(length);
        apdu.sendBytesLong(src, offset, length);		
	}

	private void writeArray(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		byte[] dst = (byte[]) storage[p1];

		short offset = (short) 0;
		short length = (short) dst.length;

		short bytesRead = apdu.setIncomingAndReceive();
		short dataOffset = apdu.getOffsetCdata();
		Util.arrayCopy(buffer, dataOffset, dst, offset, bytesRead);
		short dataLength = apdu.getIncomingLength();
		if (dataLength > length)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		short messageOffset = bytesRead;
		if (bytesRead != dataLength) {
			short received = 0;
			do {
				received = apdu.receiveBytes((short) 0);
				Util.arrayCopyNonAtomic(buffer, (short) 0, dst, messageOffset,
						received);
				messageOffset += received;
			} while (received != 0);
		}
	}

	private void clearArray(APDU apdu) {
		byte[] buffer = apdu.getBuffer();

		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		byte[] dst = (byte[]) storage[p1];

		short offset = (short) 0;
		short length = (short) dst.length;

		Util.arrayFillNonAtomic(dst, offset, length, (byte) 0);
	}
	

	private void addMedrec(APDU apdu){
		errorFlagChecking(); //prev process evaluation
		byte[] buffer = apdu.getBuffer();
		byte[] headTailIndex = (byte[]) storage[(short) (mdCapacity +2)];
		byte[] medRecIdx = (byte[]) storage[(short) (mdCapacity +3)];
		byte[] addressIdx =(byte[]) storage[(short) (mdCapacity +4)];
		byte[] statusFlag = (byte[]) storage[(short) (mdCapacity +5)];
		byte[] errorFlag = (byte[]) storage[(short) (mdCapacity+ 6)];
		short[] medrecDinamikOff = (short[]) storageOffset[2];
		//p1
		//short[] arrOfResult = findAddresstoInsert();
		short idxOfFAT = findAddresstoInsert();
		//short idxOfFAT=(short)idxtoinsert;
		headTailIndex[1] = (byte)(headTailIndex[1]+1);
		statusFlag[idxOfFAT] = (byte)1; //no valid
		byte p1 = (byte)(addressIdx[idxOfFAT]+2);	
		byte[] dst = (byte[])storage[p1];
		byte[] ts = (byte[]) storage[mdCapacity+10+p1];
		short length = (short)dst.length;
		
		
		
		
		//write data into memory per record
		short bytesRead = apdu.setIncomingAndReceive();
		short dataOffset = apdu.getOffsetCdata();
		short startOffset = (short)0; 
		short i;
		
		//adding time for delay
		byte x = (byte)2;
		byte u=(byte)3;
		//delay added
//		for(i = (short)0;i<(short)10000;i++){
//			x = u;
//		}
		while((short)bytesRead>(short)0){
			//delay added
			for(i = (short)0;i<(short)10000;i++){
				x = u;
			}
			Util.arrayCopyNonAtomic(buffer,dataOffset,dst,startOffset,bytesRead);
			//delay added
//			for(i = (short)0;i<(short)10000;i++){
//				x = u;
//			}
			startOffset+=bytesRead;
			bytesRead = apdu.receiveBytes(dataOffset);
		}
		//dst[0] = (byte)(headTailIndex[1]+48);
		if(headTailIndex[1]<(byte)10){
			dst[0] = (byte)(headTailIndex[1]+48);
		}else{
			short number = (short)headTailIndex[1];
			short k = (short)1;
			while(number>0){
				dst[k] = (byte)((number % 10)+48);
				number = (byte) (number /10);
				k--;
			}
			//dst[0] = (byte)((headTailIndex[1]&0xff)+48);
			//dst[1] = (byte)(((headTailIndex[1]>>1)&0xff) +48);
			
		}



		medRecIdx[idxOfFAT] = (byte)headTailIndex[1];
		//medRecIdx[idxOfFAT] = (byte)p1;
		statusFlag[idxOfFAT] =  (byte)2;
		
		//send response of OK
		buffer[0] = (byte)'O';
		buffer[1] = (byte)'K';
		apdu.setOutgoingAndSend((short)0, (short)2);
//		byte[] src = {(byte)'O',(byte)'K'};
//		short offset = (short) 0;
//		short lengthOK = (short) src.length;
//		apdu.setOutgoing();
//        apdu.setOutgoingLength(lengthOK);
//        apdu.sendBytesLong(src, offset, lengthOK);
	}
	

	public void updateCardData(APDU apdu){
		errorFlagChecking(); //prev process evaluation
		byte[] buffer = apdu.getBuffer();
		byte[] headTailIndex = (byte[]) storage[(short) (mdCapacity +2)];
		byte[] medRecIdx = (byte[]) storage[(short) (mdCapacity +3)];
		byte[] addressIdx =(byte[]) storage[(short) (mdCapacity +4)];
		byte[] statusFlag = (byte[]) storage[(short) (mdCapacity +5)];
		
		//p1
		//short[] arrOfResult = findAddresstoInsert();
		short idxOfFAT = findAddresstoInsert();
		byte p1 = buffer[ISO7816.OFFSET_P1];
		boolean ifExist = checkIfIndexExist((short)p1);
		if(ifExist==false){
			if(p1>=headTailIndex[0] && p1<=headTailIndex[1]){
				//short idxOfFAT=(short)idxtoinsert;
				statusFlag[idxOfFAT] = (byte)1; //no valid
				byte storagep1 = (byte)(addressIdx[idxOfFAT]+2);
				
				byte[] dst = (byte[])storage[storagep1];
				short length = (short)dst.length;
				
				
				//write data into memory per record
				short bytesRead = apdu.setIncomingAndReceive();
				short dataOffset = apdu.getOffsetCdata();
				short startOffset = (short)0;
				short i;
				
				//adding time for delay
				byte x = (byte)2;
				byte u=(byte)3;
				//delay added
				for(i = (short)0;i<(short)10000;i++){
					x = u;
				}
			
				while((short)bytesRead>(short)0){
					//delay added
					for(i = (short)0;i<(short)10000;i++){
						x = u;
					}
					Util.arrayCopyNonAtomic(buffer,dataOffset,dst,startOffset,bytesRead);
					//delay added
					for(i = (short)0;i<(short)10000;i++){
						x = u;
					}
					startOffset+=bytesRead;
					bytesRead = apdu.receiveBytes(dataOffset);
				}
				
				//Util.arrayCopyNonAtomic(buffer,dataOffset,medRecIdx,idxOfFAT,(short)1);
				
				//medRecIdx[idxOfFAT] = (byte)dst[0];
				
				//medRecIdx[idxOfFAT] = (byte)dst[0];
				medRecIdx[idxOfFAT] = (byte)p1;
				statusFlag[idxOfFAT] = (byte)2;
			}

		}
		if(ifExist==true){
			byte[] src = {(byte)'E',(byte)'X',(byte)'I',(byte)'S',(byte)'T'};
			short offset = (short) 0;
			short length = (short) src.length;
			apdu.setOutgoing();
	        apdu.setOutgoingLength(length);
	        apdu.sendBytesLong(src, offset, length);
		}else{
			byte[] src = {(byte)'O',(byte)'K'};
			short offset = (short) 0;
			short length = (short) src.length;
			apdu.setOutgoing();
	        apdu.setOutgoingLength(length);
	        apdu.sendBytesLong(src, offset, length);
		}
		

		
		
	}
	public void addressSorted(APDU apdu){
		errorFlagChecking(); // prev process evaluation
		sort((short)0,(short)(mdCapacity-1));
		//byte[] buffer = apdu.getBuffer();
		
		//byte[] src = (byte[]) storage[(short)(mdCapacity+4)]; //addressIdx
		byte[] src = {(byte)0x4F,(byte)0x4B};
		short offset = (short) 0;
		short length = (short) src.length;

		apdu.setOutgoing();
        apdu.setOutgoingLength(length);
        apdu.sendBytesLong(src, offset, length);	
	}
	
	public void sortStatusFlag(APDU apdu){
		errorFlagChecking(); // previous process evaluation
		
		tableManagement((short)0,(short)(mdCapacity-1));
		//byte[] buffer = apdu.getBuffer();
		
		//byte[] src = (byte[]) storage[(short)(mdCapacity+4)]; //addressIdx
		byte[] src = {(byte)0x4F,(byte)0x4B};
		short offset = (short) 0;
		short length = (short) src.length;

		apdu.setOutgoing();
        apdu.setOutgoingLength(length);
        apdu.sendBytesLong(src, offset, length);
	}
	

	
	public void returnIndexValues(APDU apdu){
		//return values of index in card
		//capacity(1)||headindex(1)||tailindex(1)||jumlah_yg_invalid(1)||index2_yg_invalid(flexible)
		errorFlagChecking();
		sort((short)0,(short)(mdCapacity-1));
		byte[] headTailIndex = (byte[]) storage[(short) (mdCapacity +2)];
		byte[] medRecIdx = (byte[]) storage[(short) (mdCapacity +3)];
		byte[] statusFlag = (byte[]) storage[(short) (mdCapacity +5)];
		byte[] invalidFlagArray = new byte[mdCapacity]; //store the invalid index to be returned in apdu
		//count invalid index
		short invalidFlagCounter=(short)0;
		short validFlagCounter = (short)0;
		short currentIndex = (short)0;
		boolean isSuchIndex=false;
		short i=(short)0;
		for(i=0;i<mdCapacity;i++){
			currentIndex = (short)(headTailIndex[0]+i);
			if(currentIndex>=headTailIndex[0]&& currentIndex<=headTailIndex[1]){
				//current index is inside the range of head and tail index
				for(short j=0;j<mdCapacity;j++){
					if(medRecIdx[j]==currentIndex){
						isSuchIndex = true;
						if(statusFlag[j]==(byte)1){
							//there is an index but invalid
							if(invalidFlagCounter!=(short)0){
								invalidFlagCounter = (short)(invalidFlagCounter+1);
							}
							invalidFlagArray[invalidFlagCounter] = (byte)currentIndex;
						}else{
							//else data valid
							validFlagCounter = (short)(validFlagCounter+1);
						}
						
					}
				}
				if(!isSuchIndex){
					//no such index
					invalidFlagCounter = (short)(invalidFlagCounter+1);
					invalidFlagArray[invalidFlagCounter] = (byte)currentIndex;
				}
				isSuchIndex = false;
			}//else outside range between head and tail
		}
		short leftOverIndex = (short)(mdCapacity-validFlagCounter-invalidFlagCounter);
		if(leftOverIndex>0){
			//input unknown index to invalid flag array
			for(i=0;i<leftOverIndex;i++){
				invalidFlagCounter = (short)(invalidFlagCounter+1);
				invalidFlagArray[invalidFlagCounter] = (byte)'U'; //unknown
			}
			
		}
		byte[] src = new byte[4+invalidFlagCounter];
		Util.arrayCopy(invalidFlagArray, (short)0, src, (short)4, invalidFlagCounter);
		src[0] = (byte)mdCapacity;
		src[1] = (byte)headTailIndex[0];
		src[2] = (byte)headTailIndex[1];
		src[3] = (byte)invalidFlagCounter;
		
		//byte[] src = {(byte)mdCapacity,(byte)headTailIndex[0],(byte)headTailIndex[1],(byte)invalidFlagNumber,(byte)};
		short offset = (short) 0;
		short length = (short)src.length;
		apdu.setOutgoing();
		apdu.setOutgoingLength(length);
		apdu.sendBytesLong(src,offset,length);
		
	}
	
	public void returnValidIndex(APDU apdu){
		//capacity(1)||headindex(1)||tailindex(1)||jumlah_yg_invalid(1)||index2_yg_invalid(flexible)
		errorFlagChecking();
		sort((short)0,(short)(mdCapacity-1));
		byte[] headTailIndex = (byte[]) storage[(short) (mdCapacity +2)];
		byte[] medRecIdx = (byte[]) storage[(short) (mdCapacity +3)];
		byte[] statusFlag = (byte[]) storage[(short) (mdCapacity +5)];
		byte[] validFlagArray = new byte[mdCapacity]; //store the invalid index to be returned in apdu
		//count invalid index
		//short invalidFlagCounter=(short)0;
		short validFlagCounter = (short)0;
		short currentIndex = (short)0;
		//boolean isSuchIndex=false;
		short i=(short)0;
		for(i=0;i<mdCapacity;i++){
			if(statusFlag[i]==(byte)2){
				validFlagArray[validFlagCounter] = medRecIdx[i];
				validFlagCounter = (short)(validFlagCounter+1);
				
			}
		}
		byte[] src = new byte[4+validFlagCounter];
		src[0] = (byte)mdCapacity;
		src[1] = (byte)headTailIndex[0];
		src[2] = (byte)headTailIndex[1];
		src[3] = (byte)validFlagCounter;
		Util.arrayCopy(validFlagArray, (short)0, src, (short)4, (short)(validFlagCounter+1));
		
		//byte[] src = {(byte)mdCapacity,(byte)headTailIndex[0],(byte)headTailIndex[1],(byte)invalidFlagNumber,(byte)};
		short offset = (short) 0;
		short length = (short)src.length;
		apdu.setOutgoing();
		apdu.setOutgoingLength(length);
		apdu.sendBytesLong(src,offset,length);
		
	}

	// check if a record is valid, 
	public boolean isRecordValid(short idxRecord){
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		if(statusFlag[(short)idxRecord]==(byte)1){ //N= Not valid
			return false;
		}else{
			return true; // O = open or C = close
		}
		
	}
	public void readTimeStamp(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		short length = (short) (7*p1);
		short startOffset = (short) 0;
		//byte[] src = (byte[]) storage[(short)(0+mdCapacity+10)];
		
		for(short i=0;i<p1;i++){
			byte[] timeStamp = (byte[]) storage[i+mdCapacity+10];
			Util.arrayCopy(timeStamp, (short)0, buffer, (short)startOffset, (short)7);
			startOffset = (short)(startOffset+7);
		}
		
		//7*mdcapcacity ga perlu while bytesread>0
		apdu.setOutgoingAndSend((short)0, length);
//		
//		apdu.setOutgoing();
//        apdu.setOutgoingLength(length);
//        apdu.sendBytesLong(buffer, (short)0, length);
		
	}
	public void writeTimeStamp(APDU apdu){
		byte[] buffer = apdu.getBuffer();

		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		byte[] dst = (byte[]) storage[(short)(10+mdCapacity+p1)];

		
		
		short offset = (short)0;
		
		short length = (short) 7;

		short bytesRead = apdu.setIncomingAndReceive();
		short dataOffset = apdu.getOffsetCdata();
		Util.arrayCopy(buffer, dataOffset, dst, offset, bytesRead);
		short dataLength = apdu.getIncomingLength();
		if (dataLength > length)
			ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
		short messageOffset = bytesRead;
		if (bytesRead != dataLength) {
			short received = 0;
			do {
				received = apdu.receiveBytes((short) 0);
				Util.arrayCopyNonAtomic(buffer, (short) 0, dst, messageOffset,
						received);
				messageOffset += received;
			} while (received != 0);
		}
		
	}
	//check if a record is empty
	public boolean isRecordEmpty(short idxRecord){
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		if(statusFlag[(short)idxRecord]==(byte)0){ //O = Open
			return true;
		}else{
			return false;
		}
	}
	//find an empty medrecIndex in memory 
	public short findAddresstoInsert(){
		short i=0;
		
		short[] arr = new short[2];
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		byte[] addressIdx = (byte[])storage[mdCapacity+4];
		short idxtoinsert = (short)0;
		
		boolean found = false;
		//short oldestData;
		for(i=0; i<mdCapacity; i++){
			if(!found){
				if(statusFlag[i] ==(byte)0){
					//arr[0] = (short)addressIdx[i]; //addressindextomemory
					idxtoinsert = (short)i; //fat index
					found = true;
				}
			}
		}
		for(i=0;i<mdCapacity;i++){
			if(!found){
				if(statusFlag[i]==(byte)1){
					idxtoinsert = (short)i; //fat index
					found = true;
				}
			}
		}
		if(!found){
			idxtoinsert = (short)findTheOldestRecord();
//			arr[1] =(short)headTailIndex[0]; //oldest record
//			arr[0] = (short)idxtoinsert;
		}

		
		return idxtoinsert;
	}
	public short findTheOldestRecord(){
		byte[] medrecIdx = (byte[])storage[mdCapacity+3];
		byte[] headTailIndex = (byte[])storage[mdCapacity+2];
		short i = (short)0;
		//short tempSmaller = (short)0;
		short indexSmaller = (short)0;
		headTailIndex[0] = medrecIdx[0];
		for(i=1;i<mdCapacity;i++){
			if(headTailIndex[0]>medrecIdx[i]){
				headTailIndex[0] =(byte) medrecIdx[i];
				indexSmaller = (short)i;
			}
		}
		return indexSmaller;
	}

	public void sort( short low, short high){
		//copy data to temp
		byte[] errorFlag = (byte[])storage[mdCapacity +6];
		copyAllocationTabletoBackUp();
		errorFlag[0] = (byte) 1;
		
		//quick sort implementation
		//delay added
		//adding time for delay
		byte x = (byte)2;
		byte u=(byte)3;
		for(short k = (short)0;k<(short)10000;k++){
			x = u;
		}
		byte[] medRecIdx = (byte[])storage[mdCapacity+3];
		byte[] addressIdx = (byte[])storage[mdCapacity+4];
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		byte pivot = (byte)0;
		short i = (short)0;
		short j = (short)0;
		short pi= (short)0;
		byte tempMedRecIdx= (byte)0;
		byte tempaddressIdx=(byte)0;
		byte tempstatusFlag=(byte)0;
		byte[] headTailIndex = (byte[])storage[mdCapacity+2];
		if(low<high){
			pivot = medRecIdx[(short)high];
			i = (short)(low-1);
			for(j=(short)low; j<=(short)(high-1);j++){
				if(medRecIdx[j]<=pivot){
					i++;
					tempMedRecIdx = (byte)medRecIdx[i];
					tempaddressIdx = (byte)addressIdx[i];
					tempstatusFlag = (byte)statusFlag[i];
					
					medRecIdx[i] = (byte)medRecIdx[j];
					addressIdx[i] = (byte)addressIdx[j];
					statusFlag[i] = (byte)statusFlag[j];
					
					medRecIdx[j] = (byte)tempMedRecIdx;
					addressIdx[j] = (byte)tempaddressIdx;
					statusFlag[j] = (byte)tempstatusFlag;
				}
				for(short k = (short)0;k<(short)10000;k++){
					x = u;
				}
			}
			tempMedRecIdx = (byte)medRecIdx[i+1];
			tempaddressIdx = (byte)addressIdx[i+1];
			tempstatusFlag = (byte)statusFlag[i+1];
			
			medRecIdx[i+1] = (byte)medRecIdx[high];
			addressIdx[i+1] = (byte)addressIdx[high];
			statusFlag[i+1] = (byte)statusFlag[high];
			for(short k = (short)0;k<(short)10000;k++){
				x = u;
			}
			medRecIdx[high] = (byte)tempMedRecIdx;
			addressIdx[high] = (byte)tempaddressIdx;
			statusFlag[high] = (byte)tempstatusFlag;
			
			pi=(short)(i+1);
			
			sort((short)low,(short)(pi-1));
			sort((short)(pi+1),(short)high);
		}
		
		headTailIndex[0] = (byte)medRecIdx[0]; //medrecidx ke-0
		headTailIndex[1] = (byte)medRecIdx[(mdCapacity-1)];
		for(short k = (short)0;k<(short)10000;k++){
			x = u;
		}
		errorFlag[0] = (byte)0;
		
	}
	public void tableManagement(short low, short high){
		//sort based on statusFlag
		byte[] errorFlag = (byte[])storage[mdCapacity +6];
		copyAllocationTabletoBackUp();
		errorFlag[0] = (byte) 1;
		
		//quick sort implementation
		byte[] medRecIdx = (byte[])storage[mdCapacity+3];
		byte[] addressIdx = (byte[])storage[mdCapacity+4];
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		byte pivot = (byte)0;
		short i = (short)0;
		short j = (short)0;
		short pi= (short)0;
		byte tempMedRecIdx= (byte)0;
		byte tempaddressIdx=(byte)0;
		byte tempstatusFlag=(byte)0;
		byte[] headTailIndex = (byte[])storage[mdCapacity+2];
		if(low<high){
			pivot = statusFlag[(short)high];
			i = (short)(low-1);
			for(j=(short)low; j<=(short)(high-1);j++){
				if(statusFlag[j]<=pivot){
					i++;
					tempMedRecIdx = (byte)medRecIdx[i];
					tempaddressIdx = (byte)addressIdx[i];
					tempstatusFlag = (byte)statusFlag[i];
					
					medRecIdx[i] = (byte)medRecIdx[j];
					addressIdx[i] = (byte)addressIdx[j];
					statusFlag[i] = (byte)statusFlag[j];
					
					medRecIdx[j] = (byte)tempMedRecIdx;
					addressIdx[j] = (byte)tempaddressIdx;
					statusFlag[j] = (byte)tempstatusFlag;
				}
			}
			
			tempMedRecIdx = (byte)medRecIdx[i+1];
			tempaddressIdx = (byte)addressIdx[i+1];
			tempstatusFlag = (byte)statusFlag[i+1];
			
			medRecIdx[i+1] = (byte)medRecIdx[high];
			addressIdx[i+1] = (byte)addressIdx[high];
			statusFlag[i+1] = (byte)statusFlag[high];
			
			medRecIdx[high] = (byte)tempMedRecIdx;
			addressIdx[high] = (byte)tempaddressIdx;
			statusFlag[high] = (byte)tempstatusFlag;
			
			pi=(short)(i+1);
			
			sort((short)low,(short)(pi-1));
			sort((short)(pi+1),(short)high);
		}
		//set the smallest and the highest medical record
		headTailIndex[0] = (byte)medRecIdx[0]; //medrecidx ke-0
		headTailIndex[1] = (byte)medRecIdx[(mdCapacity-1)];
		errorFlag[0] = (byte)0;
		
	}
	public void copyAllocationTabletoBackUp(){
		byte[] headTailIndex = (byte[])storage[mdCapacity+2];
		byte[] medRecIdx = (byte[])storage[mdCapacity+3];
		byte[] addressIdx = (byte[])storage[mdCapacity+4];
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		byte[] backUpMedRecIdx = (byte[])storage[mdCapacity +7];
		byte[] backUpAddressIdx= (byte[])storage[mdCapacity +8];
		byte[] backUpStatusFlag = (byte[])storage[mdCapacity +9];
		short i=(short)0;
		for(i=0;i<mdCapacity;i++){
			backUpMedRecIdx[i] = medRecIdx[i];
			backUpAddressIdx[i] = addressIdx[i];
			backUpStatusFlag[i] = statusFlag[i];
			
		}
		
		
	}
	public void copyBackUptoAllocationTable(){
		byte[] headTailIndex = (byte[])storage[mdCapacity+2];
		byte[] medRecIdx = (byte[])storage[mdCapacity+3];
		byte[] addressIdx = (byte[])storage[mdCapacity+4];
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		byte[] backUpMedRecIdx = (byte[])storage[mdCapacity +7];
		byte[] backUpAddressIdx= (byte[])storage[mdCapacity +8];
		byte[] backUpStatusFlag = (byte[])storage[mdCapacity +9];
		short i=(short)0;
		for(i=0;i<mdCapacity;i++){
			medRecIdx[i]=backUpMedRecIdx[i];
			addressIdx[i]=backUpAddressIdx[i];
			statusFlag[i]=backUpStatusFlag[i] ;
			
		}
		
	}
	
	public void errorFlagChecking(){
		//solve the error from sorting if errorFlag=1, if errorFlag=0 do nothing
		//call this function before other func
		byte[] errorFlag = (byte[])storage[mdCapacity+6];
		if(errorFlag[0]==(byte)1){
			copyBackUptoAllocationTable();
			errorFlag[0] = (byte)0;
		}
	}
	public boolean checkIfIndexExist(short idx){
		//check if index already exist on complete transaction
		byte[] medrecDinamik = (byte[])storage[mdCapacity+3];
		byte[] statusFlag = (byte[])storage[mdCapacity+5];
		short i=0;
		for(i=0;i<mdCapacity;i++){
			if(idx == medrecDinamik[i] && statusFlag[i]==(byte)2){
					return true;
			}else if(idx==medrecDinamik[i]&& statusFlag[i]==(byte)1){
				return false;
			}
		}
		return false;
	}
	
	public void prevChecking(APDU apdu){
		errorFlagChecking();
		byte[] src ={(byte)'O',(byte)'K'};
		short offset = (short) 0;
		short length = (short)src.length;
		apdu.setOutgoing();
		apdu.setOutgoingLength(length);
		apdu.sendBytesLong(src,offset,length);
		
	}
	public void findGreatestTimestampIdx(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		byte p1 = buffer[ISO7816.OFFSET_P1];
		if (p1 < 0 || p1 > storage.length)
			ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
		short maxAddress=0;		
		for(short i=1;i<p1;i++){
			byte[] currentTS =(byte[]) storage[(short)(i+mdCapacity+10)];
			byte[] maxTS = (byte[]) storage[(short)(maxAddress+mdCapacity+10)];
			if(currentTS[0]>maxTS[0]){ //yeardepan
				maxAddress = (short)i;
			}else if(currentTS[0]==maxTS[0]){ 
				if(currentTS[1]>maxTS[1]){ //yearbelakang
					maxAddress = (short)i;
				}else if(currentTS[1]==maxTS[1]){ 
					if(currentTS[2]>maxTS[2]){ //month
						maxAddress = (short)i;
					}else if(currentTS[2]==maxTS[2]){ 
						if(currentTS[3]>maxTS[3]){ //day
							maxAddress = (short)i;
						}else if(currentTS[3]==maxTS[3]){ 
							if(currentTS[4]>maxTS[4]){ //hour
								maxAddress = (short)i;
							}else if(currentTS[4]==maxTS[4]){ 
								if(currentTS[5]>maxTS[5]){ //minute
									maxAddress = (short)i;
								}else if(currentTS[5]==maxTS[5]){ 
									if(currentTS[6]>maxTS[6]){ //second
										maxAddress = (short)i;
									}
								}
							}
						}
					}
				}
			}
		}
		buffer[0] = (byte) maxAddress;
		apdu.setOutgoingAndSend((short)0, (short)1);
	}
//	
	public short findSmallestTimestampIdx(){
		short minAddress=0;		
		for(short i=1;i<mdCapacity;i++){
			byte[] currentTS =(byte[]) storage[(short)(i+mdCapacity+10)];
			byte[] maxTS = (byte[]) storage[(short)(minAddress+mdCapacity+10)];
			if(currentTS[0]<maxTS[0]){ //yeardepan
				minAddress = (short)i;
			}else if(currentTS[0]==maxTS[0]){ 
				if(currentTS[1]<maxTS[1]){ //yearbelakang
					minAddress = (short)i;
				}else if(currentTS[1]==maxTS[1]){ 
					if(currentTS[2]<maxTS[2]){ //month
						minAddress = (short)i;
					}else if(currentTS[2]==maxTS[2]){ 
						if(currentTS[3]<maxTS[3]){ //day
							minAddress = (short)i;
						}else if(currentTS[3]==maxTS[3]){ 
							if(currentTS[4]<maxTS[4]){ //hour
								minAddress = (short)i;
							}else if(currentTS[4]==maxTS[4]){ 
								if(currentTS[5]<maxTS[5]){ //minute
									minAddress = (short)i;
								}else if(currentTS[5]==maxTS[5]){ 
									if(currentTS[6]<maxTS[6]){ //second
										minAddress = (short)i;
									}
								}
							}
						}
					}
				}
			}
		}
		return minAddress;
	}
	
	public void commandOnly(APDU apdu){
		byte[] buffer = apdu.getBuffer();
		byte[] src = new byte[1];
		
		src[0] = (byte)0;
		apdu.setOutgoing();
        apdu.setOutgoingLength((short)1);
        apdu.sendBytesLong(src,(short) 0, (short)1);
	}
}