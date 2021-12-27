package com.studyolle.infra.config;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter
@NoArgsConstructor
public class PageVo {
	private int pageNo;
	private int pageSize;
	private int totalCnt;
	private int blockSize;//블럭갯수
	
	private int totalPage;
	private int startRow;
	private int endRow;
	
	private int startPage;
	private int endPage;
	private int prevPage;
	private int nextPage;
	
	private int size;
	
	public PageVo(int pageNo, int pageSize, int totalCnt, int blockSize) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.totalCnt = totalCnt;
		this.blockSize = blockSize;
		
		calcPager();
	}

	
	private void calcPager() {
		

		totalPage=((totalCnt+pageSize-1)/pageSize);
		

		if(pageNo < 1) pageNo=1;
		if(pageNo > totalPage) pageNo=totalPage;
		

		startRow = (pageNo-1) * pageSize + 1;

		endRow = pageNo * pageSize;


		if(endRow>totalCnt) {
			endRow=totalCnt;
		}
		

		startPage = (pageNo-1) / blockSize * blockSize + 1;
		

		endPage = startPage + blockSize - 1;
		

		if(endPage>totalPage) {
			endPage=totalPage;
		}
		
		prevPage=startPage-blockSize;
		nextPage=startPage+blockSize;
		
		size=pageSize;
	}
}
