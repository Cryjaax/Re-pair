package dao;

import pojo.Riparazione;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**@author Giovanni Liguori*/
public abstract class RiparazioneDAO
{
	public static ArrayList<Riparazione> doRetrieveAll()
	{
		Riparazione riparazione;
		ArrayList<Riparazione> list = new ArrayList<>();
		try
		{
			final Connection c = ConPool.getConnection();
			final PreparedStatement ps = c.prepareStatement("SELECT * FROM riparazioni");
			final ResultSet result = ps.executeQuery();
			
			while (result.next())
			{
				riparazione = new Riparazione();
				riparazione.setId(result.getInt(1));
				riparazione.setMarca(result.getString(2));
				riparazione.setModello(result.getString(3));
				riparazione.setStatus(result.getString(4));
				riparazione.setNota(result.getString(5));
				riparazione.setCosto(result.getInt(6));
				riparazione.setAssegnato(result.getInt(7));
				riparazione.setMailCliente(result.getString(8));
				list.add(riparazione);
			}
			
		}catch (Exception e) { e.printStackTrace(); }
		
		return list;
	}
	
	public static void doSave(Riparazione r)
	{
		final String s = "INSERT INTO riparazioni (`marca`, `modello`, `status`, `nota`, `costo`, `assegnato`, `mailCliente`) VALUES (?,?,?,?,?,?,?);";
		try
		{
			Connection con = ConPool.getConnection();
			PreparedStatement ps = con.prepareStatement(s);
			ps.setString(1,r.getMarca());
			ps.setString(2,r.getModello());
			ps.setString(3,r.getStatus());
			ps.setString(4,r.getNota());
			ps.setInt(5,r.getCosto());
			ps.setInt(6,r.getAssegnato());
			ps.setString(7,r.getMailCliente());
			
			ps.executeUpdate();
			
		}catch (Exception e) { e.printStackTrace(); }
	}
	
	public static Riparazione doRetrieveByID(int id)
	{
		Riparazione toReturn = null;
		final ArrayList<Riparazione> list = doRetrieveAll();
		for (Riparazione r : list)
		{
			if (r.getId() == id)
			{
				toReturn = r;
				break;
			}
		}
		return toReturn;
	}
	
	public static void doEdit(int id, String marca, String modello, String status, String nota, int costo)
	{
		if (doRetrieveByID(id) != null)
		{
			try
			{
				final Connection c = ConPool.getConnection();
				final PreparedStatement ps = c.prepareStatement("UPDATE riparazioni SET marca=?, modello=?, status=?, nota=?, costo=? WHERE id=?");
				ps.setString(1, marca);
				ps.setString(2, modello);
				ps.setString(3,status);
				ps.setString(4, nota);
				ps.setInt(5, costo);
				ps.setInt(6,id);
				ps.executeUpdate();
				
			} catch (Exception e) { e.printStackTrace(); }
			
		}
	}
	
}
