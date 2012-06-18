%.svg: %.dot
	dot -Tsvg $< > $@

all: host.svg overview.svg

sync: all
	rsync -avP . paloma.sto.spotify.net:public_html/ffs/

clean:
	rm *.svg
