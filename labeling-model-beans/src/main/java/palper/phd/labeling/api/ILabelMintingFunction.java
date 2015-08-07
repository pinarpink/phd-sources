package palper.phd.labeling.api;

import java.io.File;
import java.util.List;
import java.util.Map;

import palper.phd.labeling.model.LabelInstanceBean;

public interface ILabelMintingFunction {

	List<LabelInstanceBean> run(Map<String,File> invocationParamValues);
}
