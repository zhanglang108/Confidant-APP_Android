package com.stratagile.pnrouter.entity;

import com.stratagile.pnrouter.utils.FormatTransfer;

import java.io.Serializable;

/**
 * Created by hjk on 2018/10/9.
 */

public class SendFileData implements Serializable {
    //  int 4, short 2, byte 1, char 2, long 8, float 4   952
    private int Magic = 0x0dadc0de;
    private int Action = 1;
    private int SegSize;
    private int SegSeq;
    private int FileOffset;
    private int FileId;

    private short CRC;

    private byte SegMore;
    private byte Cotinue;

    private byte[] FileName = new byte[256];
    private byte[] FromId = new byte[77];
    private byte[] ToId = new byte[77];
    private byte[] SrcKey = new byte[256];
    private byte[] DstKey = new byte[256];
    private byte[] Porperty = new byte[1];
    private byte[] Ver = new byte[1];

    private byte[] Content = new byte[0];

    public int getMagic() {
        return Magic;
    }

    public void setMagic(int magic) {
        Magic = magic;
    }
    public int getAction() {
        return Action;
    }

    public void setAction(int action) {
        Action = action;
    }

    public int getSegSize() {
        return SegSize;
    }

    public void setSegSize(int segSize) {
        SegSize = segSize;
    }

    public int getSegSeq() {
        return SegSeq;
    }

    public void setSegSeq(int segSeq) {
        SegSeq = segSeq;
    }

    public int getFileOffset() {
        return FileOffset;
    }

    public void setFileOffset(int fileOffset) {
        FileOffset = fileOffset;
    }

    public int getFileId() {
        return FileId;
    }

    public void setFileId(int fileId) {
        FileId = fileId;
    }

    public short getCRC() {
        return CRC;
    }

    public void setCRC(short CRC) {
        this.CRC = CRC;
    }

    public byte getSegMore() {
        return SegMore;
    }

    public void setSegMore(byte segMore) {
        SegMore = segMore;
    }

    public byte getCotinue() {
        return Cotinue;
    }

    public void setCotinue(byte cotinue) {
        Cotinue = cotinue;
    }

    public byte[] getFileName() {
        return FileName;
    }

    public void setFileName(byte[] fileName) {
        System.arraycopy(fileName, 0, FileName, 0, fileName.length> FileName.length ? FileName.length : fileName.length);
    }

    public byte[] getFromId() {
        return FromId;
    }

    public void setFromId(byte[] fromId) {
        System.arraycopy(fromId, 0, FromId, 0, fromId.length > FromId.length ? FromId.length:fromId.length );
    }

    public byte[] getToId() {
        return ToId;
    }

    public void setToId(byte[] toId) {
        System.arraycopy(toId, 0, ToId, 0, toId.length > ToId.length ? ToId.length : toId.length);
    }

    public byte[] getPorperty() {
        return Porperty;
    }

    public void setPorperty(byte[] porperty) {
        System.arraycopy(porperty, 0, Porperty, 0, porperty.length > Porperty.length ? Porperty.length : porperty.length);
    }

    public byte[] getVer() {
        return Ver;
    }

    public void setVer(byte[] ver) {
        System.arraycopy(ver, 0, Ver, 0, ver.length > Ver.length ? Ver.length : ver.length);
    }

    public byte[] getContent() {
        return Content;
    }

    public void setContent(byte[] content) {
        Content = content;
        //System.arraycopy(content, 0, Content, 0, content.length);
    }
    public byte[] getSrcKey() {
        return SrcKey;
    }

    public void setSrcKey(byte[] srcKey) {

        System.arraycopy(srcKey, 0, SrcKey, 0, srcKey.length > SrcKey.length ? SrcKey.length : srcKey.length);
    }

    public byte[] getDstKey() {
        return DstKey;
    }

    public void setDstKey(byte[] dstKey) {
        System.arraycopy(dstKey, 0, DstKey, 0, dstKey.length > DstKey.length ? DstKey.length : dstKey.length);
    }

    public byte[] toByteArray() {
        byte[] magicByte = FormatTransfer.toLH(this.Magic);
        byte[] ActionByte = FormatTransfer.toLH(this.Action);
        byte[] SegSizeByte = FormatTransfer.toLH(this.SegSize);
        byte[] SegSeqByte = FormatTransfer.toLH(this.SegSeq);
        byte[] FileOffsetByte = FormatTransfer.toLH(this.FileOffset);
        byte[] FileIdByte = FormatTransfer.toLH(this.FileId);
        byte[] CRCByte = FormatTransfer.toLH(this.CRC);
        byte[] SegMoreByte = new byte[]{this.SegMore};
        byte[] CotinueByte = new byte[]{this.Cotinue};
        int length = magicByte.length + ActionByte.length + SegSizeByte.length + SegSeqByte.length + FileOffsetByte.length + FileIdByte.length + CRCByte.length + SegMoreByte.length + CotinueByte.length
                + this.FileName.length + this.FromId.length + this.ToId.length +this.SrcKey.length+this.DstKey.length+this.Porperty.length+this.Ver.length+ this.Content.length;
        System.out.println("发送文件长度："+length);
        byte[] result = new byte[length];
        int copyLength = 0;
        System.arraycopy(magicByte, 0, result, copyLength, magicByte.length);
        copyLength += magicByte.length;
        System.arraycopy(ActionByte, 0, result, copyLength, ActionByte.length);
        copyLength += ActionByte.length;
        System.arraycopy(SegSizeByte, 0, result, copyLength, SegSizeByte.length);
        copyLength += SegSizeByte.length;
        System.arraycopy(SegSeqByte, 0, result, copyLength, SegSeqByte.length);
        copyLength += SegSeqByte.length;
        System.arraycopy(FileOffsetByte, 0, result, copyLength, FileOffsetByte.length);
        copyLength += FileOffsetByte.length;
        System.arraycopy(FileIdByte, 0, result, copyLength, FileIdByte.length);
        copyLength += FileIdByte.length;
        System.arraycopy(CRCByte, 0, result, copyLength, CRCByte.length);
        copyLength += CRCByte.length;
        System.arraycopy(SegMoreByte, 0, result, copyLength, SegMoreByte.length);
        copyLength += SegMoreByte.length;
        System.arraycopy(CotinueByte, 0, result, copyLength, CotinueByte.length);
        copyLength += CotinueByte.length;
        System.arraycopy(this.FileName, 0, result, copyLength, this.FileName.length);
        copyLength += this.FileName.length;
        System.arraycopy(this.FromId, 0, result, copyLength,this.FromId.length);
        copyLength += this.FromId.length;
        System.arraycopy(this.ToId, 0, result, copyLength,this.ToId.length);
        copyLength += this.ToId.length;

        System.arraycopy(this.SrcKey, 0, result, copyLength,this.SrcKey.length);
        copyLength += this.SrcKey.length;
        System.arraycopy(this.DstKey, 0, result, copyLength,this.DstKey.length);
        copyLength += this.DstKey.length;

        System.arraycopy(this.Porperty, 0, result, copyLength,this.Porperty.length);
        copyLength += this.Porperty.length;

        System.arraycopy(this.Ver, 0, result, copyLength,this.Ver.length);
        copyLength += this.Ver.length;

        System.arraycopy(this.Content, 0, result, copyLength,this.Content.length);
        //copyLength += this.Content.length;
        /*byte[] add = new byte[]{0, 0};
        System.arraycopy(add, 0, result, copyLength,add.length);*/
        return result;
    }
}
