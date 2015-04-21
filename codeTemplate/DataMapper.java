package @packageName@;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

public interface DataMapper {

@content@
	
	

	@Select("select LAST_INSERT_ID()")
	int getLastID();
}