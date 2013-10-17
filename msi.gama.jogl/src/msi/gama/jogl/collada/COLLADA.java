package msi.gama.jogl.collada;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.opengl.util.BufferUtil;

import org.apache.commons.lang.ArrayUtils;

public class COLLADA extends DefaultHandler {
	
	private GeometryLibrary m_GeometryLibrary = new GeometryLibrary();
	private VisualScenesLibrary m_visualScenesLibrary = new VisualScenesLibrary();
	
	/* library geometry booleans*/
	private boolean inlibrary_geometries = false;
	private boolean inGeometry = false;
	private boolean inMesh = false;
	private boolean inSource = false;
	private boolean inFloatArray = false;
	private boolean inTechniqueCommon = false;
	private boolean inAccessor = false;
	private boolean inVertices = false;
	private boolean inTriangles = false;
	private boolean inP = false;
	
	/*library visual scenes booleans*/
	private boolean inlibrary_visual_scenes = false;
	private boolean inVisualScene = false;
	private boolean inNode = false;
	private boolean inInstanceGeometry = false;
	private boolean inbindMaterial = false;
	private boolean inInstanceMaterial = false;
	private boolean inbindVertexInput = false;
	
	/*temporary variables*/
	private Geometry geometry;
	private Mesh mesh;
	private Source source;
	private Float_Array floatArray;
	private TechniqueCommon techniqueCommon;
	private Accessor accessor;
	private Param param;
	private Vertices vertices;
	private Input input;
	private Triangles triangles;
	private P p;
	
	private VisualScene visualScene;
	private Node node;	
	private InstanceGeometry instanceGeometry;
	private BindMaterial bindMaterial;
	private InstanceMaterial instanceMaterial;
	private BindVertexInput bindVertexInput;
	
	private ArrayList<Integer> tempVertexIndicesArray = new ArrayList<Integer>();
	private ArrayList<Integer> tempNormalsIndicesArray = new ArrayList<Integer>();
	
	private ArrayList<FloatBuffer> trianglesPositionArray = new ArrayList<FloatBuffer>();
	private ArrayList<FloatBuffer> trianglesNormalsArray = new ArrayList<FloatBuffer>();
	private ArrayList<IntBuffer> trianglesIndicesArray = new ArrayList<IntBuffer>();
	
	/* Buffer to store data*/
	private FloatBuffer vertexBuffer;
	private FloatBuffer normalsBuffer;
	private IntBuffer indicesBuffer;

	@Override
	public void startDocument() throws SAXException {
        super.startDocument();
    }
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException 
	{
		super.characters(ch, start, length);
		
		String tempText = new String(ch,start,length);
		
		if (inFloatArray) {		
			
			String[] temp = tempText.split(" ");
        	for (int i=0; i< temp.length; i++)
        	{
        		if(!temp[i].isEmpty())
        			floatArray.getFloats().add(Float.parseFloat(temp[i]));

        	}
        	
        	source.setFloatArray(floatArray);

		}
		if(inP)
		{		
			String[] temp =tempText.split(" ");
        	for (int i=0; i< temp.length; i++)
        	{
        		if(!temp[i].isEmpty())
        			p.getIndices().add(Integer.parseInt(temp[i]));
        	}

        	
        	triangles.setP(p);
		}		
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException 
	{
		super.endElement(uri, localName, qName);
		/*
		 * Library Geometry and children
		 */
		if(qName.equalsIgnoreCase("library_geometries"))
		{
			inlibrary_geometries = false;
		}
		if(qName.equalsIgnoreCase("geometry") && inlibrary_geometries)
		{
			m_GeometryLibrary.getGeometries().add(geometry);
			inGeometry  = false;
		}
		if(qName.equalsIgnoreCase("mesh") && inGeometry)
		{
			this.geometry.setMesh(mesh);
			
			inMesh = false;
		}
		if(qName.equalsIgnoreCase("source") && inMesh)
		{
			mesh.getSources().add(source);
			
			inSource  = false;
		}
		if(qName.equalsIgnoreCase("float_array") && inSource)
		{
			inFloatArray  = false;
		}
		if(qName.equalsIgnoreCase("technique_common") && inSource)
		{
			source.setTechniqueCommon(techniqueCommon);
			
			inTechniqueCommon = false;
		}
		if(qName.equalsIgnoreCase("accessor") && inTechniqueCommon)
		{	
			techniqueCommon.setAccessor(accessor);
			
			inAccessor = false;
		}
		if(qName.equalsIgnoreCase("vertices") && inMesh)
		{		
			mesh.setVertices(vertices);
			inVertices = false;
			
		}
		if(qName.equalsIgnoreCase("triangles") && inMesh)
		{	
			mesh.getTriangles().add(triangles);
			inTriangles = false;
		}
		if(qName.equalsIgnoreCase("p") && inTriangles)
		{				
			inP = false;
		}
		
		/*
		 * Library Visual Scene and children
		 */
		if(qName.equalsIgnoreCase("library_visual_scenes"))
		{
			inlibrary_visual_scenes = false;
		}
		if(qName.equalsIgnoreCase("visual_scene") && inlibrary_visual_scenes)
		{
			m_visualScenesLibrary.getVisualScenes().add(visualScene);
			inVisualScene  = false;
		}
		if(qName.equalsIgnoreCase("node") && inVisualScene)
		{
			visualScene.setNode(node);
			
			inNode = false;			
		}
		if(qName.equalsIgnoreCase("instance_geometry") && inNode)
		{			
			node.getInstanceGeometry().add(instanceGeometry);
			
			inInstanceGeometry = false;			
		}
		if(qName.equalsIgnoreCase("bind_material") && inInstanceGeometry)
		{			
			instanceGeometry.setBinMaterial(bindMaterial);
			
			inbindMaterial = false;			
		}
		if(qName.equalsIgnoreCase("technique_common") && inbindMaterial)
		{	
			bindMaterial.setTechniqueCommon(techniqueCommon);
			
			inTechniqueCommon = false;			
		}
		if(qName.equalsIgnoreCase("instance_material") && inTechniqueCommon)
		{

			techniqueCommon.getInstanceMaterial().add(instanceMaterial);
			
			inInstanceMaterial = false;			
		}
		if(qName.equalsIgnoreCase("bind_vertex_input") && inInstanceMaterial)
		{
			instanceMaterial.getBindVertexInput().add(bindVertexInput);
			
			inbindVertexInput = false;			
		}
			
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException 
	{
		super.startElement(uri, localName, qName, attributes);
		/*
		 * Library Geometry and children
		 */
		if(qName.equalsIgnoreCase("library_geometries"))
		{
			inlibrary_geometries = true;
		}
		if(qName.equalsIgnoreCase("geometry") && inlibrary_geometries)
		{
			geometry = new Geometry();

			String ID  = attributes.getValue("id");
			geometry.setID(ID);
			
			String name = attributes.getValue("name");
			geometry.setName(name);
			
			inGeometry = true;			
		}
		if(qName.equalsIgnoreCase("mesh") && inGeometry)
		{
			mesh = new Mesh();
			inMesh = true;
		}
		if(qName.equalsIgnoreCase("source") && inMesh)
		{
			source = new Source();
			
			String ID  = attributes.getValue("id");
			source.setID(ID);
			
			inSource = true;			
		}
		if(qName.equalsIgnoreCase("float_array") && inSource)
		{
			floatArray = new Float_Array();
			
			String ID  = attributes.getValue("id");
			floatArray.setID(ID);
			
			int count  = Integer.parseInt(attributes.getValue("count"));
			floatArray.setCount(count);	
			
			inFloatArray = true;
		}
		if(qName.equalsIgnoreCase("technique_common") && inSource)
		{
			techniqueCommon = new TechniqueCommon();
			
			inTechniqueCommon = true;
		}
		if(qName.equalsIgnoreCase("accessor") && inTechniqueCommon)
		{
			accessor = new Accessor();
			
			String source = attributes.getValue("source");
			accessor.setSource(source);
			
			int count  = Integer.parseInt(attributes.getValue("count"));
			accessor.setCount(count);
			
			int stride  = Integer.parseInt(attributes.getValue("stride"));
			accessor.setStride(stride);	
			
			inAccessor = true;
		}
		if(qName.equalsIgnoreCase("param") && inAccessor)
		{
			param = new Param();
			
			String name = attributes.getValue("name");
			param.setName(name);
			
			String type = attributes.getValue("type");
			param.setType(type);
			
			accessor.getParams().add(param);
			
		}
		if(qName.equalsIgnoreCase("vertices") && inMesh)
		{
			vertices = new Vertices();
			
			String ID  = attributes.getValue("id");
			vertices.setID(ID);
			
			inVertices = true;
			
		}
		if(qName.equalsIgnoreCase("input") && inVertices)
		{
			input = new Input();
			
			String source = attributes.getValue("source");
			input.setSource(source);
			
			String semantic = attributes.getValue("semantic");
			input.setSemantic(semantic);
			
			vertices.getInputs().add(input);
		}
		if(qName.equalsIgnoreCase("triangles") && inMesh)
		{
			triangles = new Triangles();
			
			int count  = Integer.parseInt(attributes.getValue("count"));
			triangles.setCount(count);
			
			String material = attributes.getValue("material");
			triangles.setMaterials(material);
			
			inTriangles = true;
		}
		if(qName.equalsIgnoreCase("input") && inTriangles)
		{
			input = new Input();
			
			String source = attributes.getValue("source");
			input.setSource(source);
			
			String semantic = attributes.getValue("semantic");
			input.setSemantic(semantic);
			
			String offset = attributes.getValue("offset");
			input.setOffset(offset);
			
			String set = attributes.getValue("set");
			input.setSet(set);
			
			triangles.getInputs().add(input);
		}
		if(qName.equalsIgnoreCase("p") && inTriangles)
		{
			p = new P();
			
			inP = true;
		}
		/*
		 * library visual scene and its children
		 */
		if(qName.equalsIgnoreCase("library_visual_scenes"))
		{
			inlibrary_visual_scenes  = true;
		}
		if(qName.equalsIgnoreCase("visual_scene") && inlibrary_visual_scenes)
		{
			visualScene = new VisualScene();

			String ID  = attributes.getValue("id");
			visualScene.setID(ID);
			
			inVisualScene = true;			
		}
		if(qName.equalsIgnoreCase("node") && inVisualScene)
		{
			node = new Node();

			String name  = attributes.getValue("name");
			node.setName(name);
			
			inNode = true;			
		}
		if(qName.equalsIgnoreCase("instance_geometry") && inNode)
		{
			instanceGeometry = new InstanceGeometry();

			String url  = attributes.getValue("url");
			node.setName(url);
			
			inInstanceGeometry = true;			
		}
		if(qName.equalsIgnoreCase("bind_material") && inInstanceGeometry)
		{
			bindMaterial = new BindMaterial();
			
			inbindMaterial = true;			
		}
		if(qName.equalsIgnoreCase("technique_common") && inbindMaterial)
		{
			techniqueCommon = new TechniqueCommon();
			
			inTechniqueCommon = true;			
		}
		if(qName.equalsIgnoreCase("instance_material") && inTechniqueCommon)
		{
			instanceMaterial = new InstanceMaterial();

			String symbol  = attributes.getValue("symbol");
			instanceMaterial.setSymbol(symbol);
			
			String target  = attributes.getValue("target");
			instanceMaterial.setTarget(target);
			
			inInstanceMaterial = true;			
		}
		if(qName.equalsIgnoreCase("bind_vertex_input") && inInstanceMaterial)
		{
			bindVertexInput = new BindVertexInput();

			String semantic  = attributes.getValue("semantic");
			bindVertexInput.setSemantic(semantic);
			
			String inputSemantic  = attributes.getValue("input_semantic");
			bindVertexInput.setInput_semantic(inputSemantic);
			
			String inputSet  = attributes.getValue("input_set");
			bindVertexInput.setInput_set(inputSet);
			
			inbindVertexInput = true;			
		}
		
	}

	public void ColladaIntoVbo()
	{
		int nbGeometry = this.m_GeometryLibrary.getGeometries().size(); // count number of geometry elements in COLLADA file
		
		for(int i =0; i<nbGeometry;i++)
		{
			this.geometry = this.m_GeometryLibrary.getGeometries().get(i);
			
			if(this.geometry.getMesh()!=null)
			{
				this.mesh = this.geometry.getMesh();
				
				
				/* the sources that contains all the informations (vertex, normals, textcoord)*/
				int nbSources = this.mesh.getSources().size(); // count number of sources in mesh
				
				this.vertices = this.mesh.getVertices();		// get the vertices object contained in Mesh
//				String verticesID = this.vertices.getID();		// vertices ID attribute
				int nbVerticesInput = this.vertices.getInputs().size(); // number of input contained in vertices
				
				for(int j = 0; j<nbVerticesInput; j++)
				{
					this.input = this.vertices.getInputs().get(j);
					String inputSource = this.input.getSource();
					if(this.input.getSemantic().equalsIgnoreCase("position"))
					{
						for(int k = 0; k < nbSources; k++)
						{
							this.source = mesh.getSources().get(k);
							if(inputSource.contains(this.source.getID()))
							{
								this.floatArray = this.source.getFloatArray();
								int count = this.floatArray.getCount();
								this.vertexBuffer = BufferUtil.newFloatBuffer(count);
								for(int l = 0; l<count;l++)
								{
									this.vertexBuffer.put((float)this.floatArray.getFloats().get(l));
								}
								this.vertexBuffer.rewind();
								this.trianglesPositionArray.add(this.vertexBuffer);
							}							
						}		
					}
					if(this.input.getSemantic().equalsIgnoreCase("normal"))
					{
						for(int k = 0; k < nbSources; k++)
						{
							this.source = mesh.getSources().get(k);
							if(this.source.getID().equalsIgnoreCase(inputSource))
							{
								this.floatArray = this.source.getFloatArray();
								int count = this.floatArray.getCount();
								this.normalsBuffer = BufferUtil.newFloatBuffer(count);
								float[] array = ArrayUtils.toPrimitive(this.floatArray.getFloats().toArray(new Float[0]), 0.0F);
								this.normalsBuffer.put(array);
								this.trianglesNormalsArray.add(this.normalsBuffer);
							}							
						}		
					}
				} //end of vertices inputs loop	
				
				int nbTriangles = this.mesh.getTriangles().size(); // count number of triangles contained in Mesh
				for(int j=0; j<nbTriangles;j++)
				{
					Triangles triangles = this.mesh.getTriangles().get(j);
					this.p = triangles.getP();
					
					int nbInputs = triangles.getInputs().size(); //count number of input in triangles object				
					for(int k = 0; k <nbInputs;k++)
					{
						Input trianglesInput = this.triangles.getInputs().get(k);
						if(trianglesInput.getSemantic().equalsIgnoreCase("vertex"))
						{
							int offset = Integer.parseInt(trianglesInput.getOffset());
							for(int l = offset; l<p.getIndices().size();l+=nbInputs)
							this.tempVertexIndicesArray.add(p.getIndices().get(l));
						}
						if(trianglesInput.getSemantic().equalsIgnoreCase("normal"))
						{
							int offset = Integer.parseInt(trianglesInput.getOffset());
							for(int l = offset; l<p.getIndices().size();l+=nbInputs)
							this.tempNormalsIndicesArray.add(p.getIndices().get(l));
						}					
					}
				}
				this.indicesBuffer = BufferUtil.newIntBuffer(tempVertexIndicesArray.size());
				
				int[] intArray = ArrayUtils.toPrimitive(tempVertexIndicesArray.toArray(new Integer[0]));

				this.indicesBuffer.put(intArray);
				
				this.trianglesIndicesArray.add(this.indicesBuffer);
				
				this.indicesBuffer.rewind();
				
			} //end if geometry.getMesh
		}
	} //end function ColladaIntoVbo
	

	/**---------------GETTER AND SETTER FUNCTIONS-----------------------------**/
	public GeometryLibrary getGeometryLibrary() {
		return m_GeometryLibrary;
	}

	public void setGeometryLibrary(GeometryLibrary m_GeometryLibrary) {
		this.m_GeometryLibrary = m_GeometryLibrary;
	}
	
	public ArrayList<FloatBuffer> getVertexBufferArray() {
		return this.trianglesPositionArray;
	}

	public void setVertexBuffer(FloatBuffer vertexBuffer) {
		this.vertexBuffer = vertexBuffer;
	}

	public ArrayList<FloatBuffer> getNormalsBufferArray() {
		return this.trianglesNormalsArray;
	}

	public void setNormalsBuffer(FloatBuffer normalsBuffer) {
		this.normalsBuffer = normalsBuffer;
	}

	public ArrayList<IntBuffer> getIndicesBufferArray() {
		return this.trianglesIndicesArray;
	}

	public void setIndicesBuffer(IntBuffer indicesBuffer) {
		this.indicesBuffer = indicesBuffer;
	}
	
	
	
	/**
	 * Function to print all the objects contained in GeometryLibrary object after parsing
	 * the COLLADA File.
	 */
	public void printCollada()
	{
		System.out.println("<library_visual_scenes>");
		for(int i = 0; i <this.m_visualScenesLibrary.getVisualScenes().size();i++)
		{
			VisualScene tempVisualScene = this.m_visualScenesLibrary.getVisualScenes().get(i);
			System.out.println("	<visual_scene id = "+tempVisualScene.getID()+">");
			System.out.println("		<node id = "+tempVisualScene.getNode().getName()+">");
			for(int j = 0; j <tempVisualScene.getNode().getInstanceGeometry().size();j++)
			{
				InstanceGeometry tempInstanceGeometry = tempVisualScene.getNode().getInstanceGeometry().get(j);
				System.out.println("			<instance_geometry id = "+tempInstanceGeometry.getUrl()+">");
				if(tempInstanceGeometry.getBinMaterial()!=null)
				{
					System.out.println("				<bind_material>");
					System.out.println("					<technique_common>");
					for(int k = 0; k <tempInstanceGeometry.getBinMaterial().getTechniqueCommon().getInstanceMaterial().size();k++)
					{
						InstanceMaterial tempInstanceMaterial = tempInstanceGeometry.getBinMaterial().
								getTechniqueCommon().getInstanceMaterial().get(k);
						System.out.println("						<instance_material symbol = "+tempInstanceMaterial.getSymbol()
								+" target = "+tempInstanceMaterial.getTarget()+">");
						for(int l = 0; l <tempInstanceMaterial.getBindVertexInput().size();l++)
						{
							BindVertexInput tempBindVertexInput = tempInstanceMaterial.getBindVertexInput().get(l);
							System.out.println("							<bind_vertex_input semantic = "+tempBindVertexInput.getSemantic()
									+" input_semantic = "+tempBindVertexInput.getInput_semantic()
									+" input_set = "+tempBindVertexInput.getInput_set()+"/>");
						}
						System.out.println("						</instance_material>");
					}
					System.out.println("					</technique_common>");
					System.out.println("				</bind_material>");
				}
				System.out.println("			</instance_geometry>");

			}
			
			System.out.println("		</node>");
			System.out.println("	</visual_scene>");

		}
		System.out.println("</library_visual_scenes>");

		System.out.println("<library_geometries>");
		for(int i = 0; i <this.m_GeometryLibrary.getGeometries().size();i++)
		{
			Geometry tempGeometry = this.m_GeometryLibrary.getGeometries().get(i);
			System.out.println("	<geometry id = "+tempGeometry.getID()+" name = "+tempGeometry.getName()+">");
			
			Mesh mesh = tempGeometry.getMesh();
			System.out.println("		<mesh>");
			for(int j = 0; j<mesh.getSources().size();j++)
			{
				Source tempSource = mesh.getSources().get(j);
				System.out.println("			<source id = "+tempSource.getID()+">");
				
				Float_Array tempFloatArray = tempSource.getFloatArray();
				System.out.println("				<float_array id = "+tempFloatArray.getID()+
						" count = "+tempFloatArray.getCount()+
						">"+tempFloatArray.getFloats().toString()+"</float_array>");
				
				TechniqueCommon tempTechCom = tempSource.getTechniqueCommon();
				System.out.println("				<technique_common>");
				
				Accessor tempAccessor = tempTechCom.getAccessor();
				System.out.println("					<accessor source = "+tempAccessor.getSource()+
						" count = "+tempAccessor.getCount()+
						" stride = "+tempAccessor.getStride());
				for(int k = 0; k<tempAccessor.getParams().size(); k++)
				{
					Param param = tempAccessor.getParams().get(k);
					System.out.println("						<param name = "+param.getName()+
							" type = "+param.getType()+"/>");
								
				}
				System.out.println("					</accessor>");
				System.out.println("				</technique_commmon>");
				System.out.println("			</source>");

			}
			
			Vertices vertices = mesh.getVertices();
			System.out.println("			<vertices id = "+vertices.getID()+">");
			for(int l = 0; l<vertices.getInputs().size();l++)
			{
				Input input = vertices.getInputs().get(l);
				System.out.println("				<input semantic = "+input.getSemantic()+
						" source = "+input.getSource()+"/>");
			}
			System.out.println("			</vertices>");
			
			for(int m = 0; m<mesh.getTriangles().size();m++)
			{
				Triangles triangles = mesh.getTriangles().get(m);
				System.out.println("			<triangles material = "+triangles.getMaterials()+
						" count = "+triangles.getCount()+">");
				for(int n = 0; n<triangles.getInputs().size();n++)
				{
					Input input = triangles.getInputs().get(n);
					System.out.println("				<input semantic = "+input.getSemantic()+
							" source = "+input.getSource()+
							" offset = "+input.getOffset()+
							" set = "+input.getSet()+
							"/>");
				}
				
				P p = triangles.getP();
				System.out.println("				<p>"+p.getIndices().toString()+"</p>");
				System.out.println("			</triangles>");
			}
			
			System.out.println("		</mesh>");
			System.out.println("	</geometry>");
		}
		System.out.println("</library_geometries>");
				
	}
	
}
