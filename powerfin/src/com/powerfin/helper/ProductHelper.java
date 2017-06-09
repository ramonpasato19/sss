package com.powerfin.helper;

import java.sql.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.model.*;

public class ProductHelper {

	public static String getNewAccountId(Product product) {
		StringBuilder newCode = new StringBuilder();
		Integer sequence = 1;
		Integer productSequence = ProductHelper.getSequence(product);
		if (productSequence!=null)
			sequence = productSequence;
		if(product.getPrefix()!=null && !product.getPrefix().trim().isEmpty())
			newCode.append(product.getPrefix());
		if(product.getLpad()!=null && !product.getLpad().trim().isEmpty())
		{
			String lpadCharacter = product.getLpad().substring(0, 1);
			int lpadLength = product.getLpad().length();
			int sequenceLength = sequence.toString().length();
			for (int i=0; i<lpadLength-sequenceLength; i++)
			{
				newCode.append(lpadCharacter);
			}
		}
		newCode.append(sequence.toString());
		if(product.getRpad()!=null && !product.getRpad().trim().isEmpty())
			newCode.append(product.getRpad());
		if(product.getSufix()!=null && !product.getSufix().trim().isEmpty())
			newCode.append(product.getSufix());
		return newCode.toString().toUpperCase().trim();
	}
	
	public static Integer getSequence(Product product) {
		Integer sequence = null;
		Connection con = null;
		if (product.getSequenceDBName()!=null)
		{
			try {
				con = DataSourceConnectionProvider.getByComponent("Product")
						.getConnection();
				Statement stmt = con.createStatement();
				ResultSet res = stmt
						.executeQuery("select nextval('"+XPersistence.getDefaultSchema().toLowerCase()+"."+product.getSequenceDBName()+"')");
				if (res.next()) {
					sequence = new Integer(res.getString(1));
				}
				stmt.close();
				return sequence;
			} catch (Exception ex) {
				throw new SystemException(ex);
			} finally {
				try {
					con.close();
				} catch (Exception ex) {
				}
			}
		}
		return sequence;
	}
}
