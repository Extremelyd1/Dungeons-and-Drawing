package engine.loader.animatedModelLoader.dataStructures;

import java.util.List;

public class SkinningData {
	
	public final List<String> jointOrder;
	public final List<VertexSkinData> verticesSkinData;
	
	public SkinningData(List<String> jointOrder, List<VertexSkinData> verticesSkinData){
		this.jointOrder = jointOrder;
		this.verticesSkinData = verticesSkinData;
	}


}
