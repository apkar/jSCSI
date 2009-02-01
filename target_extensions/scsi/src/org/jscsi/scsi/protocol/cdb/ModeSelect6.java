//Cleversafe open-source code header - Version 1.1 - December 1, 2006
//
//Cleversafe Dispersed Storage(TM) is software for secure, private and
//reliable storage of the world's data using information dispersal.
//
//Copyright (C) 2005-2007 Cleversafe, Inc.
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
//USA.
//
//Contact Information: 
// Cleversafe, 10 W. 35th Street, 16th Floor #84,
// Chicago IL 60616
// email: licensing@cleversafe.org
//
//END-OF-HEADER
//-----------------------
//@author: John Quigley <jquigley@cleversafe.com>
//@date: January 1, 2008
//---------------------

package org.jscsi.scsi.protocol.cdb;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jscsi.scsi.protocol.util.ByteBufferInputStream;

public class ModeSelect6 extends AbstractParameterCDB
{
   public static final int OPERATION_CODE = 0x15;

   private boolean PF;
   private boolean SP;

   public ModeSelect6()
   {
      super(OPERATION_CODE);
   }

   public ModeSelect6(
         boolean pageFormat,
         boolean savePages,
         int parameterLength,
         boolean linked,
         boolean normalACA)
   {
      super(OPERATION_CODE, linked, normalACA, 0, parameterLength);
      this.PF = pageFormat;
      this.SP = savePages;
   }

   public ModeSelect6(boolean pageFormat, boolean savePages, int parameterListLength)
   {
      this(pageFormat, savePages, parameterListLength, false, false);
   }

   public void decode(byte[] header, ByteBuffer input) throws IOException
   {
      DataInputStream in = new DataInputStream(new ByteBufferInputStream(input));
      int tmp;

      int operationCode = in.readUnsignedByte();
      tmp = in.readUnsignedByte();
      this.SP = (tmp & 0x01) != 0;
      this.PF = (tmp >>> 4) != 0;
      tmp = in.readShort();
      this.setParameterLength(in.readUnsignedByte());
      super.setControl(in.readUnsignedByte());

      if (operationCode != OPERATION_CODE)
      {
         throw new IOException("Invalid operation code: " + Integer.toHexString(operationCode));
      }

   }

   public byte[] encode()
   {
      ByteArrayOutputStream cdb = new ByteArrayOutputStream(this.size());
      DataOutputStream out = new DataOutputStream(cdb);

      try
      {
         out.writeByte(OPERATION_CODE);
         out.writeByte(((this.SP ? 0x01 : 0x00) | (this.PF ? 0x10 : 0x00)));
         out.writeShort(0);
         out.writeByte((byte) this.getParameterLength());
         out.writeByte(super.getControl());

         return cdb.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException("Unable to encode CDB.");
      }
   }

   public int getOperationCode()
   {
      return OPERATION_CODE;
   }

   public int size()
   {
      return 6;
   }

   public boolean isPF()
   {
      return this.PF;
   }

   public void setPF(boolean pf)
   {
      this.PF = pf;
   }

   public boolean isSP()
   {
      return this.SP;
   }

   public void setSP(boolean sp)
   {
      this.SP = sp;
   }
}