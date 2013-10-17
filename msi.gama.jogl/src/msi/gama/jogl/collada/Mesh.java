package msi.gama.jogl.collada;

import java.util.ArrayList;

public class Mesh 
{
	private ArrayList<Source> m_sources = new ArrayList<Source>();
	private Vertices m_vertices = null;
	private ArrayList<Triangles> m_triangles = new ArrayList<Triangles>();

	
	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public ArrayList<Source> getSources() {
		return m_sources;
	}
	
	public void setSources(ArrayList<Source> m_sources) {
		this.m_sources = m_sources;
	}
	
	public Vertices getVertices() {
		return m_vertices;
	}
	public void setVertices(Vertices m_vertices) {
		this.m_vertices = m_vertices;
	}

	public ArrayList<Triangles> getTriangles() {
		return m_triangles;
	}

	public void setTriangles(ArrayList<Triangles> m_triangles) {
		this.m_triangles = m_triangles;
	}
	

}