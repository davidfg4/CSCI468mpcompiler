
class LabelGenerator
{
	private static LabelGenerator labelGenerator;
	private int labelNumber;
	private LabelGenerator()
	{
		labelNumber = 0;
	}
	
	static public LabelGenerator getLabelGenerator()
	{
		if(labelGenerator == null)
			labelGenerator = new LabelGenerator();
		return labelGenerator;
	}
	
	public String generateLabel()
	{
		return "L" + labelNumber++;
	}
}